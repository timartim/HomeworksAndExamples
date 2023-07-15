package info.kgeorgiy.ja.kornilev.implementor;
import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Implementor implements Impler, JarImpler {
    /**
     * Main method for Implementor, generates Class or writes to jar
     * @param args arguments for main, if first argument is -jar, then will write to jar file, else -- generates class
     * @throws ClassNotFoundException when a given class not found in scope
     * @throws ImplerException when implementation goes wrong
     */
    public static void main(String[] args) throws ClassNotFoundException, ImplerException {
        if(args[0].equals("-jar")){
            Class<?> clazz = Class.forName(args[1]);
            Implementor implementor = new Implementor();
            implementor.implementJar(clazz, Path.of(args[2]));
        }else if(args.length >= 2){
            Class<?> clazz = Class.forName(args[0]);
            Implementor implementor = new Implementor();
            implementor.implement(clazz, Path.of(args[1]));
        }else{
            throw new RuntimeException("Incorrect number of arguments");
        }
    }

    /**
     * Default return value for number types
     */
    private final static String numDefault = "0";
    /**
     * Default return value for boolean
     */
    private final static String boolDefault = "false";
    /**
     * Default return value for link types
     */
    private final static String linkDefault = "null";
    /**
     * Return String
     */
    private final static String ret = "return";

    /**
     * Gets a default return value of a single method
     * @param method method of a function
     * @return string, a default return value for a method, that is bein calculated by methods return type.
     *
     */
    public static String getReturnStatement(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.isPrimitive()) {
            if (returnType == boolean.class) {
                return ret + " " + boolDefault + ";";
            }
            if (returnType == void.class) {
                return ret + ";";
            }
            return ret + " " + numDefault + ";";
        } else {
            return ret + " " + linkDefault + ";";
        }

    }

    /**
     * Writes a single method into certain file.
     * Written methods have only one operator in them that returns default value of the given method type.
     * @param writer a file to be written at
     * @param method a method that should be written
     * @throws IOException if something goes wrong with a file
     */
    public static void printSingleMethod(FileWriter writer, Method method) throws  IOException {
        Class<?>[] paramTypes = method.getParameterTypes();
        int modifiers = method.getModifiers();
        StringBuilder answer = new StringBuilder();
        if (Modifier.isPublic(modifiers)) {
            answer.append("public ");
        } else if (Modifier.isPrivate(modifiers)) {
            answer.append("private ");
        } else if (Modifier.isProtected(modifiers)) {
            answer.append("protected ");
        }
        if (Modifier.isFinal(modifiers)) {
            answer.append("final ");
        }
        if (Modifier.isStatic(modifiers)) {
            answer.append("static ");
        }
        answer.append(method.getReturnType().getCanonicalName())
                .append(" ")
                .append(method.getName())
                .append("(");

        for (int i = 0; i < paramTypes.length; i++) {
            answer.append(paramTypes[i].getCanonicalName()).append(" argument").append(i);
            if (i < paramTypes.length - 1) {
                answer.append(", ");
            }
        }

        answer.append(") ");
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            answer.append("throws ");
            for (int i = 0; i < exceptionTypes.length; i++) {
                answer.append(exceptionTypes[i].getCanonicalName());
                if (i < exceptionTypes.length - 1) {
                    answer.append(", ");
                }
            }
        }
        answer.append("{ \n");
        answer.append(getReturnStatement(method));
        answer.append("} \n");
        writer.write(answer.toString());
    }

    /**
     * Generates a class, a realization of interface, into .java file.
     * @param writer a file writer that writes into ceratin  .java file.
     * @param impl an implementing interface, that written class will implement
     * @param className a name of class that will be generated
     * @throws IOException when filewriter cannot write to a given file
     *
     */
    public void writeClass(FileWriter writer, Class<?> impl, String className) throws IOException {
        String answer = "public class " + className + " implements " + impl.getCanonicalName() + ' ' +
                '{';
        writer.write(answer);
        Method[] methods = impl.getMethods();
        for (Method method : methods) {
            printSingleMethod(writer, method);
        }
        writer.write('}');
    }

    /**
     * Produces code implementing class or interface specified by provided {@code token}.
     * Generated classes have Impl suffix
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws ImplerException when implementation cannot be
     * generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if(token == null){
            throw new ImplerException("No interface provided");
        }
        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Interface is private");
        }
        if (token.isPrimitive()){
            throw new ImplerException("token is Primitive");
        }
        String tokenPath = token.getPackageName().replace('.', File.separatorChar);
        String absolutePath = root.toString() + File.separatorChar + tokenPath + File.separatorChar;
        try {
            Files.createDirectories(Paths.get(absolutePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(absolutePath + token.getSimpleName() + "Impl.java");

        final String className = token.getSimpleName() + "Impl";
        try {
            FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);
            if (!token.getPackageName().isBlank()){
                writer.write("package " + token.getPackageName() + ";\n");
            }
            writeClass(writer, token, className);
            writer.close();
        } catch (IOException e) {
            throw new ImplerException(e.getMessage());
        }
    }

    /**
     * return a class Path for a certain interface ot class
     * @param token a class(interface)
     * @return a String path for a token
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * compiles a given .java code. After sucsessful exectuion generates .class file in same directory of a given file.
     * @param token given to find classPath
     * @param root root directory of class that will be compiled
     * @param files a file that will be compiled
     * @see #getClassPath(Class)
     */
    private static void compile(Class<?> token, final Path root, final List<String> files)  {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler == null){
            throw new RuntimeException("no compiler");
        }
        final String classpath = root + File.pathSeparator + getClassPath(token);;
        final int exitCode = compiler.run(null, null, null, files.get(0), "-cp", classpath, "-encoding", StandardCharsets.UTF_8.name());
        if(exitCode != 0){
            throw new RuntimeException("wrong exit code");
        }

    }

    /**
     * Creates a jar out of a given implementation of a class and interface that it is implementing
     * @param token interface that generated class implements
     * @param pathToDirectori path to directory where generated class is located
     * @param pathToClass path to generated class
     * @param pathToCompiledClass path to class after it is bein compiled
     * @param pathToJar path to jar after creation
     * @throws IOException when JavaOutputStream does not get correct path to Jar
     */
    private void createJar(Class<?> token, Path pathToDirectori, String pathToClass, String pathToCompiledClass, Path pathToJar) throws IOException {
        compile(token, pathToDirectori, List.of(pathToClass));
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(pathToJar.toString()), new Manifest());
        try{
            String pathFromRoot = token.getPackageName().replace('.', '/') + '/' + token.getSimpleName() + "Impl.class";
            JarEntry entry = new JarEntry(pathFromRoot);
            jos.putNextEntry(entry);
            Files.copy(Path.of(pathToCompiledClass), jos);
            jos.closeEntry();
            jos.close();
        }catch (IOException e){
            jos.close();
            throw new IOException(e.getMessage());
        }

    }

    /**
     * Produces <var>.jar</var> file implementing  interface specified by provided <var>token</var>.
     * Generated jars have a same name as a given interface + "Impl" suffix.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        String tokenPath = token.getPackageName().replace('.', File.separatorChar);
        Path tempDirectori = null;
        try {
            tempDirectori = Files.createTempDirectory(jarFile.getParent(), "tempDirectori");
            implement(token, tempDirectori);
            assert tempDirectori != null;
            Path path = Path.of(tempDirectori.toString(), tokenPath + File.separatorChar, token.getSimpleName());
            Path absolutePath = Path.of(path + "Impl.java");
            Path compiledPath = Path.of(path + "Impl.class");
            createJar(token, tempDirectori ,  absolutePath.toString(), compiledPath.toString(), jarFile);
        } catch (IOException e) {
            throw new ImplerException(e.getMessage());
        }
    }
}
