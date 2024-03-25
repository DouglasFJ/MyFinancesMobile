
export interface columnData {
    dbName: string,
    isId: boolean
}

export function Column(name: string, isId:boolean = false) {
    
    return (target: any, key: any) => {
        console.log(target)
        console.log(key)

        Object.defineProperty(target, `columnNameOf_${key}`, {
            value: {dbName: name, isId: isId},
            writable: false,
            enumerable: true
        })
    }
}

export function table(name: string){

    return (target: any)=>{
        Object.defineProperty(target, "tableName", {
            value: name,
            writable: false,
            enumerable: true
        })
    }
}