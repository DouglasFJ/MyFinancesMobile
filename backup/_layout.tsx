// import * as FileSystem from 'expo-file-system';
// import * as LocalAuth from "expo-local-authentication";
// import * as SplashScreen from 'expo-splash-screen';
// import { StyleSheet, Text, View } from 'react-native';
// import { InitParams } from '../src/model/general/initParamsModel';
// import { MYFINANCES_FILE_DATABASE_NAME, PATH_SQLITE_FILES, initDefaultDatabase } from '../src/services/sqLiteService';
// import { Slot } from "expo-router";

// SplashScreen.preventAutoHideAsync();

// export default function App() {
  
//   LocalAuth.authenticateAsync().then((res)=>{
//     if (res.success)
//       SplashScreen.hideAsync()
//   })

//   onInit({
//     initDatabase: true,
//     restartDatabase: true
//   })

//   return (
//     <>
//       <Slot/>
//     </>
//   );
// }

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     backgroundColor: '#fff',
//     alignItems: 'center',
//     justifyContent: 'center',
//   },
// });

// function onInit(params: InitParams) {
//   if(params.initDatabase) database(params)

// }

// async function database(params: InitParams) {
//   // verificar se o arquivo do banco já existe e criar caso não exista
//   let dbUri = PATH_SQLITE_FILES+"/"+MYFINANCES_FILE_DATABASE_NAME+".db"
  
//   let arquivoBDExiste = (await FileSystem.getInfoAsync(dbUri)).exists
  
//   if (params.restartDatabase && arquivoBDExiste)// se restart for true deletar o bd
//     await FileSystem.deleteAsync(dbUri)

//   arquivoBDExiste = (await FileSystem.getInfoAsync(dbUri)).exists

//   if(arquivoBDExiste) return;

//   //Se arquivo não existe: 
//   await initDefaultDatabase()

// }