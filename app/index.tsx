import { StyleSheet, Text, View, Button } from "react-native";
import App from "../App";
import { EntradaRepository } from "../src/repositories/EntradaRepository";
import { Entrada } from "../src/model/entidades/Entrada";

export default function Page() {

  let r = new EntradaRepository()

  return (
    <View style={styles.container}>
      <View style={styles.main}>
        <Text style={styles.title}>MyFinances</Text>
        <Text style={styles.subtitle}>Vai dar bom</Text>
        <Button title="repository" onPress={testRepository}/>
      </View>
    </View>
  );

  function testRepository() {
    let e = new Entrada();
    e.ckEntradaFixa = 0;
    e.ckEntradaVale = 0;
    e.dtCadastro = new Date();
    e.noEntrada = "ENTRADA TESTE";
    e.vrEntrada = 5.60;
    r.save(e);
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    padding: 24,
  },
  main: {
    flex: 1,
    justifyContent: "center",
    maxWidth: 960,
    marginHorizontal: "auto",
  },
  title: {
    fontSize: 64,
    fontWeight: "bold",
  },
  subtitle: {
    fontSize: 36,
    color: "#38434D",
  },
});
