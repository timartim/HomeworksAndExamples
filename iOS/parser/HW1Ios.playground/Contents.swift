import Foundation
public struct Operator<T: Numeric> {
    public let precedence: Int
    public let associativity: Associativity
    private let function: (T, T) throws -> T

    /// Конструктор с параметрами
    /// - Parameters:
    ///   - precedence: приоритет
    ///   - associativity: ассоциативность
    ///   - function: вычислимая бинарная функция
    public init(precedence: Int, associativity: Associativity, function: @escaping (T, T) -> T) {
        self.precedence = precedence
        self.associativity = associativity
        self.function = function
    }

    /// Применить оператор
    /// - Parameters:
    ///   - lhs: первый аргумент
    ///   - rhs: второй аргумент
    /// - Returns: результат, либо исключение
    public func apply(_ lhs: T, _ rhs: T) throws -> T {
        try self.function(lhs, rhs)
    }
}

/// Оператор

/// Калькулятор
public protocol Calculator<Number> {
    /// Тип чисел, с которыми работает данный калькулятор
    associatedtype Number: Numeric

    init(operators: Dictionary<String, Operator<Number>>)

    func evaluate(_ input: String) throws -> Number
}
/// Ассоциативность оператора
public enum Associativity {
    case left, right, none
}
public enum Tokens<E:Numeric> {
    case Number(value: E?)
    case Operator(value: String)
    case LBracket, RBracket
}
extension StringProtocol {
    subscript(offset: Int) -> Character {
        self[index(startIndex, offsetBy: offset)]
    }
}
extension String: Error {}
public struct IntRealCalculator<E: Numeric & LosslessStringConvertible>: Calculator {
    public typealias Number = E;
    public let oper: Dictionary<String, Operator<E>>
    public init(operators: Dictionary<String, Operator<E>>) {
        oper = operators
    }
    private func checkToPut(acc: String, input: String, i: Int) -> Bool {
        if(acc.count == 0) {
            return true
        }
        if acc[0].isLetter && input[i].isLetter {
            return true
        }
        if (acc[0] >= "0") && (acc[0] <= "9") && (input[i] >= "0") && (input[i] <= "9") {
            return true
        }
        return false
    }
    private func checkIfOperand(acc: String) -> Bool {
        if(acc[0] >= "0") && (acc[0] <= "9") {
            return false
        }
        let checkOperand = oper[acc] != nil
        if(!checkOperand) {
            return false
        }
        return true
    }
    public func evaluate(_ input: String) throws -> E {
        var tokens: [Tokens<E>] = []
        let inputArr = input.split{$0 == " "}.map(String.init)
        let len = inputArr.count
        for word in inputArr{
            //print(word)
            var acc = ""
            var containsNumbers = true
            for j in (0...word.count - 1){
                if(!word[j].isNumber || word[j] == "(" || word[j] == ")"){
                    if(acc.count != 0) && containsNumbers{
                        tokens.append(Tokens.Number(value: E(acc)))
                        acc = ""
                    }
                    containsNumbers = false
                }
                //print("ACC:", acc, " ", containsNumbers, word[j])
                acc.append(word[j])
                switch(acc){
                case "(":
                    tokens.append(Tokens.LBracket)
                    acc = ""
                    containsNumbers = true
                case")":
                    tokens.append(Tokens.RBracket)
                    acc = ""
                    containsNumbers = true
                default:
                    if(oper.keys.contains(acc)){
                        tokens.append(Tokens.Operator(value: acc))
                        containsNumbers = true
                        acc = ""
                    }
                }
            }
            if(acc.count != 0){
                switch(acc){
                case "(":
                    tokens.append(Tokens.LBracket)
                    acc = ""
                    containsNumbers = true
                case")":
                    tokens.append(Tokens.RBracket)
                    acc = ""
                    containsNumbers = true
                default:
                    if(oper.keys.contains(acc)){
                        tokens.append(Tokens.Operator(value: acc))
                        containsNumbers = true
                        acc = ""
                    }else{
                        tokens.append(Tokens.Number(value: E(acc)))
                    }
                }
            }
        }
        var polish: [Tokens<E>] = []
        var opstack: [Tokens<E>] = []
        for token in tokens{
            switch(token){
            case .Number:
                polish.append(token)
            case .Operator(let value):
                guard let currentOp = oper[value] else{throw "unknow operator found"}
                switch(currentOp.associativity){
                case .left:
                    while(!opstack.isEmpty){
                        var breakCycle = false
                        let top = opstack[opstack.count - 1]
                        switch(top){
                        case .LBracket:
                            breakCycle = true
                        case .RBracket:
                            throw "did not expect Right bracket in opStack, input is wrong"
                        case .Operator(let savedVal):
                            guard let savedOp = oper[savedVal] else{throw "uknow operator found"}
                            if(savedOp.precedence >= currentOp.precedence){
                                polish.append(opstack.popLast()!) //в opstack всегда что-то есть, потому что условие while, и это что-то оператор, если нет, то строчка не выполнится.
                            }else{
                                breakCycle = true
                            }
                        default: throw "unknow operator found"
                        }
                        if breakCycle{
                            break
                        }
                    }
                    opstack.append(token)
                case .right:
                    while(!opstack.isEmpty){
                        var breakCycle = false
                        let top = opstack[opstack.count - 1]
                        switch(top){
                        case .LBracket:
                            breakCycle = true
                        case .RBracket:
                            throw "did not expect Right bracket in opStack, input is wrong"
                        case .Operator(let savedVal):
                            guard let savedOp = oper[savedVal] else{throw "uknow operator found"}
                            if(savedOp.precedence > currentOp.precedence){
                                polish.append(opstack.popLast()!) //в opstack всегда что-то есть, потому что условие while, и это что-то оператор, если нет, то строчка не выполнится.
                            }else{
                                breakCycle = true
                            }
                        default: throw "unknow operator found"
                        }
                        if breakCycle{
                            break
                        }
                    }
                    opstack.append(token)
                case .none:
                    opstack.append(token)
                }
            case .LBracket:
                opstack.append(token)
            case .RBracket:
                while !opstack.isEmpty{
                    var breakCycle = false
                    let top = opstack[opstack.count - 1]
                    switch top{
                    case .LBracket:
                        opstack.popLast()
                        breakCycle = true
                    default:
                        polish.append(opstack.popLast()!)
                    }
                    if breakCycle{
                        break
                    }
                }
            }
        }
        while !opstack.isEmpty{
            guard let top = opstack.popLast() else {throw "uknow operator found check string"}
            polish.append(top)
        }
        var ansStack: [E] = []
        for token in polish{
            switch token{
            case .Number(let value):
                guard let element = value else {throw "unexpected token"}
                ansStack.append(element)
            case .Operator(let value):
                guard let op = oper[value] else {throw "unknown operator found"}
                switch op.associativity{
                case .none:
                    guard let a1 = ansStack.popLast() else {throw "incorrect input"}
                    guard let a2 = ansStack.popLast() else {throw "incorrect input"}
                    var res: E? = try op.apply(a2, a1)
                    guard let result = res else {throw "incorrect input"}
                    ansStack.append(result)
                case .left:
                    guard let a1 = ansStack.popLast() else {throw "incorrect input"}
                    guard let a2 = ansStack.popLast() else {throw "incorrect input"}
                    var res: E? = try op.apply(a2, a1)
                    guard let result = res else {throw "incorrect input"}
                    ansStack.append(result)
                case .right:
                    guard let a1 = ansStack.popLast() else {throw "incorrect input"}
                    guard let a2 = ansStack.popLast() else {throw "incorrect input"}
                    var res: E? = try op.apply(a1, a2)
                    guard let result = res else {throw "incorrect input"}
                    ansStack.append(result)
                }
            default:
                throw "unexpected token found in polish"
            }
        }
        guard let answer = ansStack.popLast() else {throw "incorrect input"}
        return answer
    }
}
func unary(a : Int, b: Int) -> Int{
    return a * -1
}
func test(calculator type: (some Calculator<Int>).Type) {
    let calculator = type.init(operators: [
        "abc": Operator(precedence: 10, associativity: .left, function: +),
        "-": Operator(precedence: 10, associativity: .left, function: -),
        "dfb": Operator(precedence: 20, associativity: .left, function: *),
        "/": Operator(precedence: 20, associativity: .left, function: /),
        ])
    let result1 = try! calculator.evaluate("((1abc1)dfb(1abc1)abc((1abc1)dfb1)/2)")
    print(result1)
    
}
test(calculator: IntRealCalculator<Int>.self)

