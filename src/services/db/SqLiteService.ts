import * as SQLite from "expo-sqlite";
import * as FileSystem from "expo-file-system";

const PATH_SQLITE_FILES = `${FileSystem.documentDirectory}'SQLite'`
export const DATABASE_NAME = "MyFinances"

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
async function openDatabase(name: string) {
    name += ".db"

    if (!(await FileSystem.getInfoAsync(PATH_SQLITE_FILES)).exists) {
        await FileSystem.makeDirectoryAsync(PATH_SQLITE_FILES);
    }

    return SQLite.openDatabase(name)
}

export async function select() {
    let db = await openDatabase(DATABASE_NAME)
}