import CacheEntity from "../cache/CacheEntity"

export interface ColumnData {
    name: string,
    isId: boolean
}

export function Column(name: string, isId:boolean = false) {
    
    return (target: any, key: any) => {

        CacheEntity.mapColumnsName[key] = {name: name, isId: isId};
    }
}

export function Table(name: string){

    return (target: any)=>{
        CacheEntity.mapTablesName[target.name] = name;
    }
}