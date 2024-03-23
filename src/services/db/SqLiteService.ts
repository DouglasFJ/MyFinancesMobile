import * as SQLite from "expo-sqlite";
import * as FileSystem from "expo-file-system";
import { Asset } from "expo-asset";

export const PATH_SQLITE_FILES = `${FileSystem.documentDirectory}'SQLite'`
export const MYFINANCES_FILE_DATABASE_NAME = "MyFinances"
// export const MYFINANCES_DATABASE_NAME = "MY_FINANCES_DATABASE" //TODO: excluir dps caso não tenha uso
const LOCAL_DDL_FILE = "./assets/db/ddl-myfinances.sql"
const LOCAL_DML_FILE = "./assets/db/dml-myfinances.sql"

/**
 * Método para abrir/criar um banco SQLite
 * 
 * caso o arquivo do banco não exista ele será criado no caminho:
 * ${FileSystem.documentDirectory}/SQLite/${name}.db
 * 
 * @param name nome do arquivo do banco de dados
 * 
 * @returns objeto de manipulação do banco de dados
 */
export async function openDatabase(name: string) {
    name += ".db"

    if (!(await FileSystem.getInfoAsync(PATH_SQLITE_FILES)).exists) {
        await FileSystem.makeDirectoryAsync(PATH_SQLITE_FILES);
    }
    console.debug(`Opening database in path: ${FileSystem.documentDirectory}SQLite/${name}`)
    return SQLite.openDatabase(name)
}

export async function select() {
    let db = await openDatabase(MYFINANCES_FILE_DATABASE_NAME)

}

/**
 * Essa função inicializa o banco de dados e todo o schema de forma padrão do sistema
 * 
 */
export async function initDefaultDatabase() {
    let sqlDDL: string = "";
    let sqlDML: string = "";

    //lendo os arquivos com o SQL
    try {
        const ddlAsset = Asset.fromModule(require(LOCAL_DDL_FILE))
        const dmlAsset = Asset.fromModule(require(LOCAL_DML_FILE))

        await ddlAsset.downloadAsync()
        await dmlAsset.downloadAsync()

        sqlDDL = ddlAsset.localUri ? await FileSystem.readAsStringAsync(ddlAsset.localUri) : ""
        sqlDML = dmlAsset.localUri ? await FileSystem.readAsStringAsync(dmlAsset.localUri) : ""
        
    } catch (error) {
        console.error("Erro ao ler o arquivo sql: " + error)
    }

    // executando SQLs
    let db = await openDatabase(MYFINANCES_FILE_DATABASE_NAME)
    db.transaction((t) => {

        t.executeSql(sqlDDL, [],
            (tr, r) => {
                console.debug("DDL executado com sucesso: "+r)

                tr.executeSql(sqlDML, [],
                    (trx, rs)=>{
                        console.debug("DML executado com sucesso: " + rs)
                    }
                ) 
            },
            (trx, error) => {
                console.error("Erro ao executar o DDL: "+error)
                return false;
            }
        )
    }, (error)=>{
        console.error("Erro ao iniciar uma transação SQL: "+error)
    })
}