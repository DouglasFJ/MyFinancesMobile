import { StyleSheet, Text, View, Button } from 'react-native';
import { Asset, useAssets } from 'expo-asset';
import * as FileSystem from 'expo-file-system';
import { InitParams } from './src/model/initParamsModel';
import { MYFINANCES_FILE_DATABASE_NAME, PATH_SQLITE_FILES, initDefaultDatabase } from './src/services/db/sqLiteService';

export default function App() {

  onInit({
    initDatabase: true,
    restartDatabase: true
  })

  return (
    <View style={styles.container}>
      <Text>Meu primeiro app mobile com REACT NATIVE</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

function onInit(params: InitParams) {
  if(params.initDatabase) database(params)

}

async function database(params: InitParams) {
  // verificar se o arquivo do banco já existe e criar caso não exista
  let dbUri = PATH_SQLITE_FILES+"/"+MYFINANCES_FILE_DATABASE_NAME+".db"
  
  if (params.restartDatabase)// se restart for true deletar o bd
    await FileSystem.deleteAsync(dbUri)

  let arquivoBDExiste = (await FileSystem.getInfoAsync(dbUri)).exists

  if(arquivoBDExiste) return;

  //Se arquivo não existe: 
  await initDefaultDatabase()

}