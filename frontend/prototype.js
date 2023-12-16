// 1
const getNewObjWithPrototype = (obj) => {
    return Object.create(obj)
}

// 2
const getEmptyObj = () => {
    return Object.create(null)
}

// 3
const setPrototypeChain = ({ programmer, student, teacher, person }) => {
    Object.setPrototypeOf(programmer, student);
    Object.setPrototypeOf(student, teacher);
    Object.setPrototypeOf(teacher, person);
}

// 4
const getObjWithEnumerableProperty = () => {
    let ans = {}
    Object.defineProperty(ans, 'name', {
        value: 'Alex',
    });
    Object.defineProperty(ans, 'age', {
        value: 18,
        enumerable: true
    });
    Object.defineProperty(ans, 'work', {
        value: 'empty',
    });
    return ans
}

// 5
const getWelcomeObject = (person) => {
    let obj = getNewObjWithPrototype(person)
    Object.defineProperty(obj, 'voice', {
        value: () => {
            return `Hello, my name is ${person.name}. I am ${person.age}.`
        }
    })
    return obj
}

// 6
class Singleton {
    static instance;
    static id;
    constructor(id) {
        if (!Singleton.instance) {
            this.id = id;
            Singleton.instance = this;
        } else {
            return Singleton.instance;
        }
    }
}

// 7
const defineTimes = () => {
    Number.prototype.times = function(action) {
        for (let i = 1; i <= this.valueOf(); i++) {
            action(i, this.valueOf());
        }
    };
}

// 8
const defineUniq = () => {
    Object.defineProperty(Array.prototype, 'uniq', {
        get() {
            let st = new Set()
            for(let element of this){
                st.add(element)
            }
            let ans = []
            for(let element of st){
                ans.push(element)
            }
            return ans
        }
    })
}

// 9
const defineUniqSelf = () => {
    Object.defineProperty(Array.prototype, 'uniqSelf', {
        get() {
            let st = new Set(this)
            let ans = []
            for(let element of st){
                ans.push(element)
            }
            this.length = 0
            for(let element of ans){
                this.push(element)
            }
            return ans;
        }
    })
}
module.exports = {
    getNewObjWithPrototype,
    getEmptyObj,
    setPrototypeChain,
    getObjWithEnumerableProperty,
    getWelcomeObject,
    Singleton,
    defineTimes,
    defineUniq,
    defineUniqSelf,
}