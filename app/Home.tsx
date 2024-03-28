import { StyleSheet, Text, View, Button, ScrollView, Pressable } from "react-native";
import HomeIcon from "../src/ui/icons/HomeIcon";
// import { EntradaRepository } from "../src/repositories/EntradaRepository";
import { Entrada } from "../src/model/entidades/Entrada";
import colors from "../src/ui/colors";

function NavBar(props: any) {
  
  return(
    <View style={[styles.navBar]}>
      <Pressable >
        <HomeIcon color={colors.cinza[800]} height="100" width="100" />
      </Pressable>
    </View>
  )

}

export default function Home({navigation}: any) {

  // let r = new EntradaRepository()

  return (
    <View style={styles.container}>
      <ScrollView>
        <Text style={[styles.txt]} >MyFinances</Text>
      </ScrollView>      
      <NavBar />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    backgroundColor: colors.cinza[100]
  },
  txt:{
    color: colors.cinza[800]
  },
  navBar: {
    height: 85.33,
  }
});
