import { StyleSheet, Text, View, Button } from 'react-native';
import { Asset, useAssets } from 'expo-asset';
import * as FileSystem from 'expo-file-system';
import { InitParams } from './src/model/initParamsModel';

export default function App() {

  onInit({})

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
  database(params)

}

function database(params: InitParams) {
  // verificar se o banco de dados já existe e criar caso não exista
}