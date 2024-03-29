import { Button, Pressable, StyleSheet, Text, View } from "react-native";
import Colors from "../../ui/Colors";
import { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import NunitoText from "../../shared/NunitoText";
import MyFinancesIcon from "../../ui/icons/MyFinancesIcon";
import { PropsWithChildren, useState } from "react";
import SetaUmSvg from "../../ui/svg/SetaUmSvg";

const nomeMesNumeroMes = [
    "Janeiro",
    "Fevereiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
    "Janeiro",
]

export default function InicioScreen({navigation, route}: BottomTabScreenProps<any>) {
    
    const [mesSelecionado, setMesSelecionado] = useState(1)

    return(
        <>
            <Button title="dev helper" onPress={()=>{navigation.navigate("devHelpReact")}}/>
            <View style={[styles.container]}>
                <View style={[styles.topPage]}>
                    <MyFinancesIcon height="40" width="40" 
                    firstColor={Colors.verde[500]} secondColor={Colors.amarelo[500]} threeColor={Colors.vermelho[600]} />
                    <Pressable style={styles.buttonSelectMes}>
                        <NunitoText style={[{color: Colors.cinza[800], fontSize: 15, marginRight: 8}]}>
                            {nomeMesNumeroMes[mesSelecionado-1]}
                        </NunitoText>
                        <SetaUmSvg color={Colors.cinza[800]} height="10" width="16"/>
                    </Pressable>
                </View>
                <Section backgroundColor={Colors.vermelho[50]}>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "center"
                    }}>
                        <View style={{
                            alignItems: "center"
                        }} >
                            <NunitoText style={{color: Colors.cinza[950]}}>Saldo em contas</NunitoText>
                            <NunitoText style={{
                                fontSize: 23
                            }}>R$ 526,32</NunitoText>
                        </View>
                    </View>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-evenly"
                    }}>
                        <View style={{alignItems: "center"}}>
                            <NunitoText style={{color: Colors.cinza[950]}}>Entrada</NunitoText>
                            <NunitoText style={{
                                fontSize: 23
                            }}>R$ 526,32</NunitoText>
                        </View>
                        <View style={{alignItems: "center"}}>
                            
                        </View>
                        <View style={{alignItems: "center"}}>
                            <NunitoText style={{color: Colors.cinza[950]}}>Saída</NunitoText>
                            <NunitoText style={{
                                fontSize: 23
                            }}>R$ 526,32</NunitoText>
                        </View>
                    </View>
                </Section>
            </View>
        </>
    )

}

function Section({children, backgroundColor}:any) {
    
    return(
        <View style={[styles.section, {backgroundColor: backgroundColor}]} >
            {children}
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        padding: 20
    },
    topPage:{
        flexDirection: "row",
        alignItems:"center",
        justifyContent:"space-between",
        marginBottom: 25
    },
    buttonSelectMes: {
        backgroundColor: Colors.vermelho[50],
        paddingVertical: 8,
        paddingHorizontal: 20,
        borderRadius: 50,
        shadowColor: Colors.cinza[950],
        elevation: 5,
        flexDirection: "row",
        alignItems: "center"
    },
    section: {
        elevation: 5,
        borderRadius: 10,
        padding: 15
    }
})
