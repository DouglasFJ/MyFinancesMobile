import { SQLiteDatabase } from "expo-sqlite";
import { MYFINANCES_FILE_DATABASE_NAME, openDatabase } from "../services/sqLiteService";
import { IRepository } from "./IRepository";
import { Entrada } from "../model/entidades/Entrada";
import { ColumnData } from "../decorators/DatabaseDecorators";
import CacheEntity from "../cache/CacheEntity";

export class EntradaRepository implements IRepository<Entrada, number>{


    async save(model: Entrada): Promise<Entrada> {
        let r = new Promise<Entrada>(async (resolve, reject) => {

            openDatabase(MYFINANCES_FILE_DATABASE_NAME)
                .then(async db => {
                    await db.transactionAsync(
                        async t => {
                            await t.executeSqlAsync(this.constructInsert(model)+"SELECT * FROM MFTB001_ENTRADA;").then(r=>console.log(r))
                            
                        }
                    )
                })
                .catch(error => {
                    console.error("Method 'save' in 'EntradaRepository':")
                    console.error(error)
                    throw error
                })

        })

        return r;
    }

    async update(model: Entrada): Promise<Entrada> {
        throw new Error("Method not implemented.");
    }
    async delete(id: number): Promise<void> {
        throw new Error("Method not implemented.");
    }
    async findById(id: number): Promise<Entrada> {
        throw new Error("Method not implemented.");
    }
    async findAll(params: { sort: string; limit: number; }): Promise<Entrada[]> {
        throw new Error("Method not implemented.");
    }
    async existsById(id: number): Promise<boolean> {
        throw new Error("Method not implemented.");
    }
    async count(): Promise<number> {
        throw new Error("Method not implemented.");
    }

    private constructInsert(model: Entrada): string {

        let sqlString = "INSERT INTO "
        let valuesString = "VALUES ("
        sqlString += CacheEntity.mapTablesName[Entrada.name] + " ("

        let props = Object.keys(model);

        props.forEach((prop, index, arr) => {

            let columnData: ColumnData = CacheEntity.mapColumnsName[prop]
            if (columnData.isId) return;

            sqlString += index == arr.length - 1 ? columnData.name + ") " : columnData.name + ", ";

            let valueProp = model[prop]

            let sData;
            if (Object.getPrototypeOf(valueProp) == Date.prototype) {
                console.log(valueProp)
                sData = (valueProp as Date).toISOString()
            } else {
                sData = valueProp;
            }

            valuesString += index == arr.length - 1 ? `'${sData}') ` : `'${sData}', `;
        })

        sqlString += valuesString;
        sqlString += ";"

        console.debug("sql construido: \n" + sqlString)

        return sqlString;
    }

}