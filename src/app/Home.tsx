import { BottomTabBarProps, createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import React from "react";
import { Pressable, StyleSheet, View } from "react-native";
import colors from "../ui/Colors";
import AddIcon from "../ui/icons/AddIcon";
import CaixaIcon from "../ui/icons/CaixaIcon";
import CartaoIcon from "../ui/icons/CartaoIcon";
import HomeIcon from "../ui/icons/HomeIcon";
import MesIcon from "../ui/icons/MesIcon";
import CadastroScreen from "./home/Cadastro";
import CaixaScreen from "./home/Caixa";
import CartoesScreen from "./home/Cartoes";
import InicioScreen from "./home/Inicio";
import MesScreen from "./home/Mes";
import NunitoText from "../shared/NunitoText";

const HomeTab = createBottomTabNavigator();

const screenNames = {
  inicio: "início",
  mes: "mês",
  cadastros: "cadastros",
  cartoes: "cartões",
  caixa: "caixa"
}

function TabBar(
  { state, descriptors, navigation }: BottomTabBarProps
) {



  return (
    <View style={[styles.navBar]}>
      {
        state.routes.map((route, index, array) => {

          let isFocused = state.index === index;

          let colorPrimary = isFocused ? colors.cinza[100] : colors.cinza[800];
          let colorSecondary = isFocused ? colors.cinza[800] : colors.cinza[100];

          let stylePressable = ({ pressed }: any) => [
            styles.buttonNavBar,
            {
              opacity: pressed ? 0.6 : 1,
            },
          ]
          let viewStyle = {
            backgroundColor: colorSecondary,
          }

          if (route.name != screenNames.cadastros) {
            return (
              <View key={index} style={[viewStyle, styles.viewButtonTab]}>
                <Pressable onPress={() => { navigation.navigate(route.name) }} style={stylePressable} >
                  {decidirIcone(route.name, colorPrimary, colorSecondary)}
                  <NunitoText style={{ color: colorPrimary }} >{route.name}</NunitoText>
                </Pressable>
              </View>
            )
          } else {
            return (
              <View key={index} style={[styles.buttonNavBar, { transform: [{ translateY: -25 }] }]} >
                <Pressable onPress={() => { navigation.navigate(route.name) }} style={[styles.buttonAddCadastro, { backgroundColor: isFocused ? colors.verde[800] : colors.verde[600] }]}>
                  <AddIcon color={colors.vermelho[50]} height="25" width="25" />
                </Pressable>
                <NunitoText style={{ color: colors.cinza[800] }} >cadastros</NunitoText>
              </View>
            )
          }

        })
      }
    </View>
  )

  function decidirIcone(routeName: string, colorPrimary: string, colorSecondary: string) {
    switch (routeName) {
      case screenNames.inicio:
        return (<HomeIcon color={colorPrimary} height="30" width="30" />)
      case screenNames.mes:
        return (<MesIcon colorPrimary={colorPrimary} colorSecondary={colorSecondary} height="30" width="30" />)
      case screenNames.cadastros:
        return (<AddIcon color={colors.vermelho[50]} height="25" width="25" />)
      case screenNames.cartoes:
        return (<CartaoIcon colorPrimary={colorPrimary} colorSecondary={colorSecondary} height="30" width="30" />)
      case screenNames.caixa:
        return (<CaixaIcon colorPrimary={colorPrimary} colorSecondary={colorSecondary} height="30" width="30" />)
      default:
        break;
    }
  }
}

export default function Home({ navigation }: any) {


  return (
    <View style={styles.container}>
      <HomeTab.Navigator initialRouteName={screenNames.inicio}
        tabBar={TabBar} screenOptions={{ headerShown: false }}>
        <HomeTab.Screen name={screenNames.inicio} component={InicioScreen} />
        <HomeTab.Screen name={screenNames.mes} component={MesScreen} />
        <HomeTab.Screen name={screenNames.cadastros} component={CadastroScreen} />
        <HomeTab.Screen name={screenNames.cartoes} component={CartoesScreen} />
        <HomeTab.Screen name={screenNames.caixa} component={CaixaScreen} />
      </HomeTab.Navigator>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    backgroundColor: colors.cinza[100],
    position: "relative"
  },
  txt: {
    color: colors.cinza[800]
  },
  navBar: {
    height: 70,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-evenly",
    borderTopColor: colors.cinza[300],
    borderTopWidth: 2
  },
  buttonNavBar: {
    alignItems: "center",
  },
  buttonAddCadastro: {
    width: 60,
    height: 45,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 65,
    borderColor: colors.cinza[100],
    borderWidth: 2
  },
  viewButtonTab: {
    width: 60,
    height: 60,
    padding: 5,
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
  }
});
