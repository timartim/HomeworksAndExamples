load-file "parser.clj"                                      ;почему то не работает
;12-ое дз -- начиная с 433 строчки

(defn constant [a]
  (fn [mp] (fn [const] const) a)
  )
(defn variable [s]
  (fn [mp] (get mp s)))
(defn applySign [& args]
  (fn [mp] (apply (first args) (map (fn [a] (a mp)) (second args)))))
(defn add [& args]
  (fn [mp] ((applySign + args) mp))
  )

(defn subtract [& args]
  (fn [mp] ((applySign - args) mp))
  )
(defn multiply [& args]
  (fn [mp] ((applySign * args) mp))
  )
; Не смог разобраться как упростить деление на 0, избегая ошибки Division by zero, не смог применить асбтракцию applySign
(defn divide [& args]
  (if (== (count args) 1)
    (fn [mp] (if (== ((first args) mp) 0) (/ (+ ((first args) mp) 1)) (/ ((first args) mp))))
    (fn [mp] (/ ((first args) mp) (apply * (map (fn [a] (if (== (a mp) 0) (+ (a mp) 1) (a mp))) (rest args)))))
    )
  )
;PowLog (31-33)
(defn pow [& args]
  (if (== (count args) 1)
    (fn [mp] ((first args) mp))
    (fn [mp] (Math/pow ((first args) mp) ((second args) mp)))
    )
  )

(defn log [& args]
  (if (== (count args) 1)
    (fn [mp] ((first args) mp))
    (fn [mp] (/ (Math/log (Math/abs ((second args) mp))) (Math/log (Math/abs ((first args) mp)))))
    )
  )
;MeanVarn (36-37)
(defn mean [& args]
  (fn [mp] (apply + (map (fn [a] (* (/ 1 (count args)) (a mp))) args))))
(defn varn [& args]
  (fn [mp] (apply + (map (fn [a] (* (- (a mp) ((apply mean args) mp)) (- (a mp) ((apply mean args) mp)) (/ 1 (count args)))) args))))

(defn negate [a]
  (fn [mp] (* (a mp) -1))
  )
(defn exp [a]
  (fn [mp] (Math/exp (a mp)))
  )
(defn ln [a]
  (fn [mp] (Math/log (a mp)))
  )
(def m {'negate negate '+ add '- subtract '* multiply '/ divide 'mean mean 'varn varn 'pow pow 'log log 'exp exp 'ln ln})

(defn parse [a]
  (cond
    (= (type a) java.lang.Double) (constant a)
    (= (type a) clojure.lang.Symbol) (variable (name a))
    (= (type a) clojure.lang.PersistentList) (apply (get m (first a)) (map parse (rest a)))
    )
  )
(defn parseFunction [args]
  (parse (read-string args))
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;HW-11

(definterface ExprInterface
  (^String toString [])
  (^Number evaluate [mp])
  (^String toStringSuffix [])
  (^String toStringInfix [])
  (diff [var])
  )
(defn evaluate [expression mp]
  (.evaluate expression mp)
  )
(defn toString [expression]
  (.toString expression)
  )
(defn toStringSuffix [expression]
  (.toStringSuffix expression)
  )
(defn toStringInfix [expression]
  (.toStringInfix expression)
  )
(defn toStringAbstraction [function exprList]
  (str "(" function " " (reduce (fn [a b] (str a " " b)) (map toString exprList)) ")")
  )
(defn toStringSuffixAbstraction [function exprList]

  (str "(" (reduce (fn [a b] (str a " " b)) (map toStringSuffix exprList)) " " function ")")
  )
(defn toStringInfixAbstraction [function exprList]
  ;(println exprList)
  (str "(" (toStringInfix (first exprList)) " " function " " (toStringInfix (second exprList)) ")")
  )
(defn evaluateAbstraction [fun exprList mp]
  (apply fun (map (fn [expr] (.evaluate expr mp)) exprList))
  )
(defn diff [expression name]
  ;(println (.diff expression name))
  (.diff expression name)
  )
(deftype ConstantMethods [value]
  ExprInterface
  (toStringSuffix [this] (str value))
  (toStringInfix [this] (str value))
  (toString [this] (str value))
  (evaluate [this mp] value)
  (diff [this name] (ConstantMethods. 0))
  )
(defn Constant [value]
  (ConstantMethods. value)
  )
(deftype VariableMethods [name]
  ExprInterface
  (toString [this] name)
  (toStringSuffix [this] name)
  (toStringInfix [this] name)
  (evaluate [this mp] (get mp (clojure.string/lower-case (get name 0))))
  (diff [this diffName] (if (= name diffName) (Constant 1) (Constant 0)))
  )
(defn Variable [name]
  (VariableMethods. name)
  )

(deftype AddMethods [exprList]
  ExprInterface
  (toString [this] (if (> (count exprList) 0) (toStringAbstraction "+" exprList) (str "(+ )")))
  (toStringInfix [this] (toStringInfixAbstraction "+" exprList))
  (toStringSuffix [this] (toStringSuffixAbstraction "+" exprList))
  (evaluate [this mp] (evaluateAbstraction + exprList mp))
  (diff [this diffName] (AddMethods. (map (fn [expr] (.diff expr diffName)) exprList)))
  )
(defn Add [& args]
  ;(println args)
  (AddMethods. args)
  )
(deftype SubtractMethods [exprList]
  ExprInterface
  (toString [this] (toStringAbstraction "-" exprList))
  (toStringInfix [this] (toStringInfixAbstraction "-" exprList))
  (toStringSuffix [this] (toStringSuffixAbstraction "-" exprList))
  (evaluate [this mp] (evaluateAbstraction - exprList mp))
  (diff [this diffName] (SubtractMethods. (map (fn [expr] (.diff expr diffName)) exprList)))
  )
(defn Subtract [& args]
  (SubtractMethods. args)
  )
(deftype MultiplyMethods [exprList]
  ExprInterface
  (toString [this] (if (> (count exprList) 0) (toStringAbstraction "*" exprList) (str "(* )")))
  (toStringSuffix [this] (toStringSuffixAbstraction "*" exprList))
  (toStringInfix [this] (toStringInfixAbstraction "*" exprList))
  (evaluate [this mp] (evaluateAbstraction * exprList mp))
  (diff [this diffName] (cond
                          (> (count exprList) 1) (Add (MultiplyMethods. (list (.diff (first exprList) diffName)
                                                                              (MultiplyMethods. (rest exprList))))
                                                      (MultiplyMethods. (list (.diff (MultiplyMethods. (rest exprList)) diffName)
                                                                              (first exprList))))
                          (== (count exprList) 1) (.diff (first exprList) diffName)
                          :else (println (type (first exprList)))
                          ))
  )
(defn Multiply [& args]
  (MultiplyMethods. args)
  )
;(println  (diff (Multiply (list (Constant 1) (Variable "x"))) "x"))
(deftype NegateMethods [expression]
  ExprInterface
  (toString [this] (str "(negate " (.toString expression) ")"))
  (toStringSuffix [this] (str "(" (.toStringSuffix expression) " negate)"))
  (toStringInfix [this] (str "negate(" (.toStringInfix expression) ")"))
  (evaluate [this mp] (* -1 (.evaluate expression mp)))
  (diff [this diffName] (Multiply (Constant -1.0) (.diff expression diffName)))
  )
(defn Negate [expression]
  (NegateMethods. expression)
  )
(deftype DivideMethods [exprList]
  ExprInterface
  (toString [this] (toStringAbstraction "/" exprList))
  (toStringSuffix [this] (toStringSuffixAbstraction "/" exprList))
  (toStringInfix [this] (toStringInfixAbstraction "/" exprList))
  (evaluate [this mp]
    ;(print "DIVIDE EVALUATE: ")
    ;(println this)
    (if (== (count exprList) 1)
      (if (== (.evaluate (first exprList) mp) 0) (* 0.0) (/ 1 (.evaluate (first exprList) mp)))
      (if (== (evaluateAbstraction * (rest exprList) mp) 0) (/ 1 (+ (evaluateAbstraction * (rest exprList) mp) 1))
                                                            (/ (.evaluate (first exprList) mp) (evaluateAbstraction * (rest exprList) mp)))
      )
    )
  (diff [this diffName] (cond
                          (> (count exprList) 1) (DivideMethods.
                                                   (list
                                                     (Subtract
                                                       (Multiply (.diff (first exprList) diffName) (MultiplyMethods. (rest exprList)))
                                                       (Multiply (.diff (MultiplyMethods. (rest exprList)) diffName) (first exprList)))
                                                     (Multiply (MultiplyMethods. (rest exprList)) (MultiplyMethods. (rest exprList)))))
                          (== (count exprList) 1) (DivideMethods.
                                                    (list (Negate
                                                            (.diff (first exprList) diffName))
                                                          (Multiply (first exprList) (first exprList))))

                          )
    )
  )

(defn Divide [& args]
  (DivideMethods. args)
  )
;;32-33MOD
(deftype ExpMethods [expression]
  ExprInterface
  (toString [this] (str "(exp " (.toString expression) ")"))
  (evaluate [this mp] (Math/exp (evaluate expression mp)))
  (diff [this diffname] (Multiply (ExpMethods. expression) (.diff expression diffname)))

  )
(defn Exp [arg]
  (ExpMethods. arg)
  )
(deftype LnMethods [expression]
  ExprInterface
  (toString [this] (str "(ln " (.toString expression) ")"))
  (evaluate [this mp] (Math/log (Math/abs (evaluate expression mp))))
  (diff [this diffname] (Multiply (Divide expression) (.diff expression diffname)))
  )
(defn Ln [expression]
  (LnMethods. expression)
  )
(deftype MeanMethods [exprList]
  ExprInterface
  (toString [this] (toStringAbstraction "mean" exprList))
  (evaluate [this mp] (apply + (map (fn [a] (* (/ 1 (count exprList)) (.evaluate a mp))) exprList)))
  (diff [this diffname] (.diff (apply Add (map (fn [a] (Multiply
                                                         (Divide (Constant (count exprList))) a)) exprList)) diffname))
  )
(defn Mean [& args]
  (MeanMethods. args)
  )
(deftype VarnMethods [exprList]
  ExprInterface
  (toString [this] (toStringAbstraction "varn" exprList))
  (evaluate [this mp] (apply + (map (fn [a] (*
                                              (- (.evaluate a mp) (.evaluate (MeanMethods. exprList) mp))
                                              (- (.evaluate a mp) (.evaluate (MeanMethods. exprList) mp))
                                              (/ 1 (count exprList)))) exprList)))
  (diff [this diffname] (.diff (apply Add (map (fn [a] (Multiply (Subtract a (MeanMethods. exprList)) (Subtract a (MeanMethods. exprList)) (Divide (Constant (count exprList))))) exprList)) diffname))
  )
(defn Varn [& args]
  (VarnMethods. args)
  )
(deftype IPowMethods [exprList]
  ExprInterface
  (toString [this] (if (> (count exprList) 0) (toStringAbstraction "**" exprList) (str "(** )")))
  (toStringSuffix [this] (toStringSuffixAbstraction "**" exprList))
  (toStringInfix [this] (toStringInfixAbstraction "**" exprList))
  (evaluate [this mp] (Math/pow (evaluate (first exprList) mp) (evaluate (second exprList) mp)))
  )
(defn IPow [& args]
  (IPowMethods. args)
  )
(deftype ILogMethods [exprList]
  ExprInterface
  (toString [this] (if (> (count exprList) 0) (toStringAbstraction "//" exprList) (str "(// )")))
  (toStringSuffix [this] (toStringSuffixAbstraction "//" exprList))
  (toStringInfix [this] (toStringInfixAbstraction "//" exprList))
  (evaluate [this mp] (evaluate (Divide (Ln (second exprList)) (Ln (first exprList))) mp))
  )
(defn ILog [& args]
  (ILogMethods. args)
  )
(def objectMap {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'mean Mean 'varn Varn 'exp Exp 'ln Ln})
(defn getObject [a mp]
  (cond
    (= (type a) java.lang.Double) (Constant a)
    (= (type a) clojure.lang.Symbol) (Variable (name a))
    (= (type a) clojure.lang.PersistentList) (apply (get objectMap (first a)) (map (fn [s] (getObject s objectMap)) (rest a)))
    )
  )
(defn parseObject [args]
  (getObject (read-string args) objectMap)
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;12
(defn -return [value tail] {:value value :tail tail})       ;создает мапу
(def -valid? boolean)                                       ; проверяет не нил ли результат
(def -value :value)
(def -tail :tail)


(defn _empty [value] (partial -return value))

(defn _char [p]                                             ; принимает предикат p, если символы ему подошл возвращает value -- первый символ tail -- оствшийся
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))       ;применят f к value r и оставляет tail, если r != nil

(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
         ((force b) (-tail ar)))))))                        ;сначала скармливает строку парсеру a, его tail скармливает строке b, к результату применяет функцию f

(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))               ;пытается пропарсить первым парсером, если не удается, парсит вторым

(defn _parser [p]                                           ;принимает парсер p , который принимает только символ с кодом 0
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0001})) (str input \u0001))))) ;? какой то парсер
(mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"]) ;?



(defn +char [chars] (_char (set chars)))                    ;более удобный _char , принимает строку
(defn +char-not [chars] (_char (comp not (set chars))))     ; символы, которые не равны указанным
(defn +map [f parser] (comp (_map f) parser))               ;применяет функцию к парсеру
(def +ignore (partial +map (constantly 'ignore)))           ; игнорирует результат парсера

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))            ;conj но игнорирует игнор, если игнор возвращает коллекцию

(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))         ;комбинирует несколько парсеров в один, последоветльно будет применять парсеры

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps))) ;? применяет функцию к результатам парсеров

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps)) ;? возвращает только n-ый из аргументов

(defn +or [p & ps]
  (reduce (partial _either) p ps))                          ;последовательно пытается применять несколько парсеров, пока не удасться сделать хотя бы с одним из них

(defn +opt [p]
  (+or p (_empty nil)))                                     ;пытается применить парсер p, если ничего не удалось возвращает nil

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec))) ;* из регулярных выражений

(defn +plus [p] (+seqf cons p (+star p)))                   ;+ из регулярных выражений

(defn +str [p] (+map (partial apply str) p))                ;делает str из результата парсера

(def +parser _parser)                                       ;?


(defn +rules [defs]                                         ;?
  (cond
    (empty? defs) ()
    (seq? (first defs)) (let [[[name args body] & tail] defs]
                          (cons
                            {:name name :args args :body body}
                            (+rules tail)))
    :else (let [[name body & tail] defs]
            (cons
              {:name name :args [] :body body :plain true}
              (+rules tail)))))

(defmacro defparser [name & defs]                           ;?
  (let [rules (+rules defs)
        plain (set (map :name (filter :plain rules)))]
    (letfn [(rule [{name :name, args :args, body :body}] `(~name ~args ~(convert body)))
            (convert [value]
              (cond
                (seq? value) (map convert value)
                (char? value) `(+char ~(str value))
                (contains? plain value) `(~value)
                :else value))]
      `(def ~name (letfn ~(mapv rule rules) (+parser (~(:name (last rules)))))))))

(defn +ignore-nil [p]
  (+map #(if (nil? %) 'ignore %) p)
  )
(def *digit (+char "0123456789."))
(def *letters (+char "xyzXYZ"))
(def *signs (+char "+-/*"))
(def *multdiv (+char "*/"))
(def *plminus (+char "+-"))
(def *brackets (+char "()"))
(defn sign [s tail]
  (if (= \- s)
    (cons s tail)
    tail)
  )

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))
(def *number (+seqn 0 *ws (+map (comp Constant read-string) (+str (+seqf sign (+opt (+char "+-")) (+plus *digit)))) *ws))
(def *variable (+seqn 0 *ws (+map Variable (+str (+plus *letters))) *ws))
(def *op (+str (+plus *signs)))
(def *br (+str (+plus *brackets)))
(def PLUSvariables
  (+plus *letters)
  )
(def parserMap {\+ Add \- Subtract \/ Divide \* Multiply "negate" Negate "**" IPow "//" ILog})
(defn getParser [& p]
  (apply (get parserMap (last p)) (butlast p))
  )
(def *negate
  (+str (+seq (+char "n") (+char "e") (+char "g") (+char "a") (+char "t") (+char "e")))
  )
(def *ilog
  (+str (+seq (+char "*") (+char "*")))
  )
(def *ipow
  (+str (+seq (+char "/") (+char "/")))
  )
(def *parseConstantVariable
  (+seqf (partial getParser) *ws (+ignore (+char "(")) *ws (+or *number *variable (delay *parseConstantVariable)) *ws
         (+or *number *variable (delay *parseConstantVariable) *negate) *ws (+ignore-nil (+opt (+or *signs *negate))) *ws (+ignore (+char ")")) *ws)
  )

(def *parseNegate
  (+seqf (partial getParser) *ws (+ignore (+char "(")) *ws (+or *number *variable (delay *parseNegate)
                                                                (delay *parseConstantVariable)) *ws *negate *ws (+ignore (+char ")")) *ws)
  )

(defn *ignore-whitespace [expr]
  (+seqn 0 *ws expr *ws)
  )
(def *ignore-brackets
  (+ignore (+star *brackets))
  )
(def *null
  (+map (constantly \u) (+seq (+plus (+char-not ""))))
  )
;My operations
(def operationsPriority {'Add 1 'Subtract 1 'Multiply 2 'Divide 2})
(def charactarObject {\+ Add \- Subtract \* Multiply \/ Divide "negate" Negate "**" IPow "//" ILog})
(def charactarPriority {\+ 1 \- 1 \* 2 \/ 2 "negate" 6 "**" 4 "//" 4 \( 0})
(defn myconj [coll1 coll2]
  (apply conj coll1 coll2))

;парсит выражения на лексемы с помощью функций приведенных на лекции
(def infixParser
  (+seq *ws (+ignore-nil (+opt (+char "(")))*ws
        (+ignore-nil (+opt (+char "("))) *ws (+ignore-nil (+opt (+or *variable *number))) *ws (+ignore-nil (+opt (+or *ipow *ilog *signs *negate)))
        *ws (+ignore-nil (+opt (+or (+seq *ws (+char "(") *ws (delay infixParser) *ws) *number *variable)))
        (+ignore-nil (+opt (+seq *ws (+or *ipow *ilog *signs *negate) *ws (delay infixParser)))
                     ) *ws (+ignore-nil (+opt (+char ")")))*ws (+ignore-nil (+opt (+char ")")))
        (+ignore-nil (+opt (+seq *ws (+or *ipow *ilog *signs *negate) *ws (delay infixParser) *ws (+ignore-nil (+opt (+char ")")))))))
  )
;собирает лексемы распаршенные infix parser в colllexemes, возвращает вектор лексем
(defn getLexemes [p collLexemes]
  (cond
    (== (count p) 0) collLexemes
    (= (type (first p)) java.lang.Character) (myconj (conj collLexemes (first p)) (getLexemes (rest p) collLexemes))
    (= (type (first p)) clojure.lang.PersistentVector) (myconj (myconj collLexemes (getLexemes (first p) collLexemes))
                                                               (getLexemes (rest p) collLexemes))
    (= (type (first p)) clojure.lang.PersistentList) (myconj (myconj collLexemes (getLexemes (first p) collLexemes))
                                                             (getLexemes (rest p) collLexemes))
    ;(== (type (first p) ) ConstantMethods) (conj collLexemes (first p))
    ;(== (type (first p)) VariableMethods) (conj collLexemes (first p))
    :else (myconj (conj collLexemes (first p)) (getLexemes (rest p) collLexemes))
    )
  )
;добавляет все операции внутри скобок в стек(вектор) ответа
(defn addTillRbracket [ans stackOperation numOfExpr]
  (if (= (count stackOperation) 0)
    (vector ans stackOperation numOfExpr)
    )
  (if (= (type (last stackOperation)) java.lang.Character)
    (if (= (last stackOperation) \()
      (vector (pop stackOperation) ans numOfExpr)
      (addTillRbracket (conj ans (last stackOperation)) (pop stackOperation) (- numOfExpr 1)))
    (addTillRbracket (conj ans (last stackOperation)) (pop stackOperation) (- numOfExpr 1))
    )
  )

(defn removeAllLeatprAbst [f stackOperation ans op numOfExpr]
  (f (pop stackOperation) (conj ans (last stackOperation)) op (- numOfExpr 1))
  )
;добавляет операции в стек ответа, пока не встретит операцию либо < либо <=(зависит от операции) по приоритету
(defn removeAllLeastPriority [stackOperation ans op numOfExpr]
  (if (== (count ans) 0) (vector stackOperation ans numOfExpr)) ;если нечего парсить, возвращаем коллекции
  (if (== numOfExpr 0) (vector stackOperation ans numOfExpr)
                       (if (= (type (last stackOperation)) java.lang.Character) ;(+-/*)
                         (if (= (last stackOperation) \()   ;если скобка -- останавливаемся и возвращаем ответ + оставшиеся операции
                           (vector stackOperation ans numOfExpr)
                           (if (<= (get charactarPriority op) (get charactarPriority (last stackOperation)))
                             (removeAllLeatprAbst removeAllLeastPriority stackOperation ans op numOfExpr) ;добавляем все элементы в стек ответа пока
                                                                                                          ; не встретим собку или операцию с меньшим приоритетом
                             (vector stackOperation ans numOfExpr)) ;возвращаем ответ + оставшиеся опреации
                           )
                         (if (= (type (last stackOperation)) java.lang.String) ;+- тоже самое, но только для операций с множествами символов
                           (cond
                             (= (last stackOperation) "negate") (if (< (get charactarPriority op) (get charactarPriority (last stackOperation)))
                                                                  (removeAllLeatprAbst removeAllLeastPriority stackOperation ans op numOfExpr)
                                                                  (vector stackOperation ans numOfExpr))
                             :else (if (< (get charactarPriority op) (get charactarPriority (last stackOperation)))
                                     (removeAllLeatprAbst removeAllLeastPriority stackOperation ans op numOfExpr)
                                     (vector stackOperation ans numOfExpr))
                             )
                           )
                         )
                       )
  )
;собирает оствшиеся операции в стек ответа
(defn putLeast [coll1 coll2]

  (if (== (count coll2) 0)
    coll1
    (putLeast (conj coll1 (last coll2)) (pop coll2))
    )
  )

(defn infixToSuffixRecur [f p stackOperation ans numOfExpr] ;случай negate
  (f (rest p) (conj (first (removeAllLeastPriority stackOperation ans (first p) numOfExpr)) (first p))
                        (second (removeAllLeastPriority stackOperation ans (first p) numOfExpr))
                        (+ 1 (nth (removeAllLeastPriority stackOperation ans (first p) numOfExpr) 2)) ;если negate -- запускаемся снова и добавляем negate в вектор ответа
                        )
  )
(defn infixToSuffixNegate [f p stackOperation ans numOfExpr]
  (f (rest p) (conj (first (removeAllLeastPriority stackOperation ans (first p) numOfExpr)) (first p))
                        (second (removeAllLeastPriority stackOperation ans (first p) numOfExpr))
                        (+ 1 (nth (removeAllLeastPriority stackOperation ans (first p) numOfExpr) 2))
                        )
  )
;с помощью функции removeAllLeastPriority и addTillRbracket, а также putLeast превращает массив выражения инфиксной формы в массиф выражения суффиксной
(defn infixToSuffixLexemes [p stackOperation ans numOfExpr]
  (cond
    (== (count p) 0) (putLeast ans stackOperation)
    (= (type (first p)) java.lang.Character) (cond
                                               (= (first p) \() (infixToSuffixLexemes (rest p) (conj stackOperation (first p)) ans numOfExpr)
                                               (= (first p) \)) (infixToSuffixLexemes (rest p)
                                                                                      (first (addTillRbracket ans stackOperation numOfExpr))
                                                                                      (second (addTillRbracket ans stackOperation numOfExpr))
                                                                                      (nth (addTillRbracket ans stackOperation numOfExpr) 2)) ;собираем что внутри скобок
                                               :else (infixToSuffixRecur infixToSuffixLexemes p stackOperation ans numOfExpr)
                                               )

    (= (type (first p)) java.lang.String) (cond
                                            (= (first p) "negate") (infixToSuffixNegate infixToSuffixLexemes p stackOperation ans numOfExpr)
                                            (= (first p) "**") (infixToSuffixRecur infixToSuffixLexemes p stackOperation ans numOfExpr)
                                            (= (first p) "//") (infixToSuffixRecur infixToSuffixLexemes p stackOperation ans numOfExpr)
                                            )


    :else (infixToSuffixLexemes (rest p) stackOperation (conj ans (first p)) numOfExpr)
    )
  )
;2 раза pop к stack
(defn pop-pop [stack]
  (pop (pop stack))
  )
;получить конструктор обьекта по символу или строке
(defn getExpression [arr]
  (get parserMap (first arr))
  )
(defn convAbstractionPoppop [f arr stack]
  (f (rest arr) (conj (pop-pop stack) ((getExpression arr) (last (pop stack)) (last stack)))) ;попает два элемента из суфФормы, один
                                                                                  ; элемент из операций и превращает их в обьект который кладет в стек

  )
(defn convAbstractionPop [f arr stack]
  (f (rest arr) (conj (pop stack) ((getExpression arr)  (last stack)))) ;для negate
  )
;преврашает массив лексем суффиксной формы в обьект
(defn convertToExpression [arr stack]
  (cond
    (== (count arr) 0) (first stack)                        ;возвращаем ответ
    (= (type (first arr)) java.lang.Character) (convAbstractionPoppop convertToExpression arr stack)
    (= (type (first arr)) java.lang.String) (cond
                                              (= (first arr) "negate") (convAbstractionPop convertToExpression arr stack)
                                              (= (first arr) "**") (convAbstractionPoppop convertToExpression arr stack)
                                              (= (first arr) "//") (convAbstractionPoppop convertToExpression arr stack)

                                              )
    :else (convertToExpression (rest arr) (conj stack (first arr)))
    )
  )
;easy
(def exprParser
  (+or *number *variable *parseConstantVariable *parseNegate)
  )
;easy
(defn parseObjectSuffix [s]
  (-value (exprParser s))
  )
(defn parseObjectInfix [s]
  ;(println (infixToSuffixLexemes (getLexemes (-value (infixParser s)) []) [] [] 0))
  (convertToExpression (infixToSuffixLexemes (getLexemes (-value (infixParser s)) []) [] [] 0) [])
  )



