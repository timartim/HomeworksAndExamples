function makePotionsRoom() {
    return storage;
}
const storage = {
    // хранилище кладовки
    store: new Map(),
    add: function (shelveName, potion) {
        if(!this.store.has(shelveName)){
            this.store.set(shelveName, []);
        }
        this.store.get(shelveName).push(potion);
    },

    // Возвращает зелье, если оно есть на любой из полок. Зелье убирается из кладовки (с любой из полок, где есть зелье)
    takePotion: function (namePotion) {
        for (let key of this.store.keys()){
            for (var element of this.store.get(key)){
                if(element.name === namePotion){
                    var idx = this.store.get(key).indexOf(element);
                    this.store.get(key).splice(idx, 1);
                    return element
                }
            }
        }
    },

    // Использует зелье (вызывая у него функцию "use"). Зелье убирается из кладовки (с любой из полок, где есть зелье).
    usePotion: function (namePotion) {
        for (let key of this.store.keys()){
            for (var element of this.store.get(key)){
                if(element.name === namePotion){
                    element.use();
                    var idx = this.store.get(key).indexOf(element);
                    this.store.get(key).splice(idx, 1);
                    return element
                }
            }
        }
    },

    // Возвращает все зелья с полки. Содержимое полки не меняется
    getAllPotionsFromShelve: function (shelveName) {
        if(!this.store.has(shelveName)){
            return [];
        }
        ans = []
        for(let potions of this.store.get(shelveName)){
            ans.push(potions)
        }
        return ans;
    },

    // Возвращает все зелья кладовки. Содержимое полок не меняется
    getAllPotions: function () {
        ans = []
        for (let key of this.store.keys()){
            for (var element of this.store.get(key)){
                ans.push(element)
            }
        }
        return  ans;
    },
    // Возвращает все зелья с полки. Полка остается пустой
    takeAllPotionsFromShelve: function (shelveName) {
        ans = [];
        if(!this.store.has(shelveName)){
            return ans;
        }
        for(var value of this.store.get(shelveName)){
            ans.push(value);
        }
        this.store.set(shelveName, []);
        return ans
    },

    // Использует все зелья с указанной полки. Полка остается пустой
    useAllPotionsFromShelve: function (shelveName) {
        ans = [];
        if(!this.store.has(shelveName)){
            return ans;
        }
        for(var value of this.store.get(shelveName)){
            value.use();
            ans.push(value);
        }
        this.store.set(shelveName, []);
        return ans
    },

    // Возвращает зелья с истекшим сроком хранения. Метод убирает такие зелья из кладовки.
    // revisionDay - день, в который происходит проверка сроков хранения
    clean: function (revisionDay) {
        var expired = [];
        for (var potions of this.store.values()) {
            for (let i = 0; i < potions.length; i++) {
                if (potions[i].created.getDate() + potions[i].expirationDays > revisionDay.getDate()) {
                    expired.push(potions[i]);
                    potions.splice(i, 1);
                }
            }
        }
        return expired;
    },

    // возвращает число - сколько уникальных зелий находится в кладовке
    uniquePotionsCount() {
        var mySet = new Set();
        for(var key of this.store.keys()){
            for(var element of this.store.get(key)){
                mySet.add(element.name)
            }
        }
        return mySet.size
    }
};

module.exports = makePotionsRoom;