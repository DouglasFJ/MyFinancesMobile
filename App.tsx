import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { dispositivoPossuiAutenticaoLocal, requerirAutenticacao } from './src/services/auth/AuthService';
import { useState } from 'react';
import * as LocalAuth from "expo-local-authentication";

export default function App() {
  const [isAuth, setIsAuth] = useState<boolean>(false)

  console.debug(isAuth)
  if(!isAuth){
    LocalAuth.authenticateAsync({
      promptMessage: "Autenticar para acessar o app",
    }).then(resultAuth=>{
      setIsAuth(resultAuth.success)
    })
  }

  return (
    <View style={styles.container}>
      <Text>Meu primeiro app mobile com REACT NATIVE</Text>
      <StatusBar style="auto" />
      <Text>Está autenticado: {isAuth? "true": "false"}</Text>
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
