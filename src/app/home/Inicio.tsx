import { Button, Pressable, ScrollView, StyleProp, StyleSheet, Text, TextStyle, View } from "react-native";
import Colors from "../../ui/Colors";
import { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import NunitoText from "../../shared/NunitoText";
import MyFinancesIcon from "../../ui/icons/MyFinancesIcon";
import { Children, PropsWithChildren, ReactNode, useState } from "react";
import SetaUmSvg from "../../ui/svg/SetaUmSvg";
import OlhoAbertoSvg from "../../ui/svg/OlhoAbertoSvg";
import OlhoFechadoSvg from "../../ui/svg/OlhoFechadoSvg";
import DadosSensiveisText from "../../shared/DadosSensiveisText";
import AlimentacaoIcon from "../../ui/icons/AlimentacaoIcon";
import SetaDoisSvg from "../../ui/svg/SetaDoisSvg";
import { CategoriaGastoEnum } from "../../enums/CategoriaGastoEnum";
import InvestimentoIcon from "../../ui/icons/InvestimentoIcon";
import PiramideIcon from "../../ui/icons/PiramideIcon";
import MoedaIcon from "../../ui/icons/MoedaIcon";
import MedicacaoIcon from "../../ui/icons/MedicacaoIcon";

const oColors: any = Colors;

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

export default function InicioScreen({ navigation, route }: BottomTabScreenProps<any>) {

    const [mesSelecionado, setMesSelecionado] = useState(1)
    const [mostrarValores, setMostrarValores] = useState(true)

    return (
        <>
            <Button title="dev helper" onPress={() => { navigation.navigate("devHelpReact") }} />
            <ScrollView style={[styles.container]}>
                <View style={[styles.topPage]}>
                    <MyFinancesIcon height="40" width="40"
                        firstColor={Colors.verde[500]} secondColor={Colors.amarelo[500]} threeColor={Colors.vermelho[600]} />
                    <Pressable style={styles.buttonSelectMes}>
                        <NunitoText style={[{ color: Colors.cinza[800], fontSize: 15, marginRight: 8 }]}>
                            {nomeMesNumeroMes[mesSelecionado - 1]}
                        </NunitoText>
                        <SetaUmSvg color={Colors.cinza[800]} height="10" width="16" />
                    </Pressable>
                </View>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "center",
                        marginBottom: 20
                    }}>
                        <View style={{
                            alignItems: "center"
                        }} >
                            <NunitoText style={{ color: Colors.cinza[950] }}>Saldo em contas</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 23,
                                color: Colors.cinza[950]
                            }} isVisible={mostrarValores} >R$ 526,32</DadosSensiveisText>
                        </View>
                    </View>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-evenly",
                        marginBottom: 20
                    }}>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.verde[600] }}>Entrada</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 20,
                                color: Colors.verde[950]
                            }} isVisible={mostrarValores}>R$ 1230,00</DadosSensiveisText>
                        </View>
                        <View style={{ alignItems: "center", flex: 1 }}>
                            <Pressable style={{
                                width: 30,
                                height: 30,
                                alignItems: "center",
                                justifyContent: "center",
                                transform: [{ translateY: -15 }]
                            }} onPress={() => {
                                setMostrarValores(!mostrarValores)
                            }}>
                                {
                                    mostrarValores ?
                                        <OlhoAbertoSvg height="24" width="36" colorPrimary={Colors.cinza[800]} colorSecondary={Colors.vermelho[50]} /> :
                                        <OlhoFechadoSvg height="24" width="36" color={Colors.cinza[800]} />
                                }

                            </Pressable>
                        </View>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.vermelho[600] }}>Saída</NunitoText>
                            <DadosSensiveisText isVisible={mostrarValores} style={{
                                fontSize: 20,
                                color: Colors.vermelho[950]
                            }}>R$ 703,68</DadosSensiveisText>
                        </View>
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.verde[50]} >
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-between",
                        marginBottom: 20
                    }}>
                        <View style={{flex: 1}}>
                            <AlimentacaoIcon height="35" width="35" firstColor={Colors.verde[950]} secondColor={Colors.verde[50]} thirdColor={Colors.verde[50]}/>
                        </View>
                        <View style={{
                            alignItems: "center",
                            flex: 3
                        }} >
                            <NunitoText style={{ color: Colors.cinza[950], fontSize:10 }}>Saldo vales alimentação</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 23,
                                color: Colors.cinza[950]
                            }} isVisible={mostrarValores} >R$ {1800-1453},00</DadosSensiveisText>
                        </View>
                        <Pressable style={{flex: 1, alignItems:"flex-end"}}>
                            <SetaDoisSvg height="20" width="18" color={Colors.cinza[800]}/>
                        </Pressable>
                    </View>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-between",
                        marginBottom: 20
                    }}>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.verde[600] }}>Entrada</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 20,
                                color: Colors.verde[950]
                            }} isVisible={mostrarValores}>R$ 1800,00</DadosSensiveisText>
                        </View>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.vermelho[600] }}>Saída</NunitoText>
                            <DadosSensiveisText isVisible={mostrarValores} style={{
                                fontSize: 20,
                                color: Colors.vermelho[950]
                            }}>R$ 1453,68</DadosSensiveisText>
                        </View>
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-between",
                        marginBottom: 10
                    }}>
                        <NunitoText style={{color: Colors.cinza[800], fontSize:15}}>Falta pagar</NunitoText>
                        <Pressable>
                            <SetaDoisSvg height="20" width="18" color={Colors.cinza[800]}/>
                        </Pressable>
                    </View>
                    <View style={{
                        minHeight: 60,
                        flexDirection: "row",
                        flexWrap: "wrap"
                    }}>
                        <Gasto width={140} title="Caixa Emergencial" valor={150} categoria={CategoriaGastoEnum.INVESTIMENTO}>
                            <DadosSensiveisText style={{
                                color: oColors.verde[700],
                                fontSize: 13
                            }} isVisible={mostrarValores}>R$ 150</DadosSensiveisText>
                        </Gasto>
                        {/* <Gasto width={140} title="Caixa Emergencial" valor={150} categoria={CategoriaGastoEnum.EXTRAS}/>
                        <Gasto width={140} title="Pão de queijo" valor={6} categoria={CategoriaGastoEnum.ALIMENTACAO}/> */}
                    </View>
                </Section>
                    <NunitoText>Últimos gastos</NunitoText>
                <Section>

                </Section>
            </ScrollView>
        </>
    )

}

function Gasto(
    {children, title, valor, categoria, width}:
    {children:any, title: string, valor:number, categoria?: CategoriaGastoEnum, width:number}
) {
    
    let {colorName, icone} = decidirIconeCor(categoria);

    return (
        <View style={{
            flexDirection: "row",
            width: width,
            overflow: "hidden",
            alignItems: "center",
            marginRight: 5,
            marginBottom: 10
        }}>
            {icone}
            <View style={{marginLeft:10}}>
                <NunitoText style={{
                    color: oColors[colorName][950],
                    fontSize: 13,
                    fontWeight: "700"
                }}>{title}</NunitoText>
                {children}
            </View>
        </View>
    )

    function decidirIconeCor(categoriaGasto?: CategoriaGastoEnum) {
        let color;
        let icone;

        switch (categoriaGasto) {
            case CategoriaGastoEnum.INVESTIMENTO:
                color = "verde"
                icone = <InvestimentoIcon height="35" width="35" 
                        firstColor={Colors.verde[950]}
                        secondColor={Colors.verde[300]} 
                        thirdColor={Colors.verde[300]}/>
                break;
            case CategoriaGastoEnum.ALIMENTACAO:
                color = "amarelo"
                icone = <AlimentacaoIcon height="35" width="35" 
                        firstColor={Colors.amarelo[950]}
                        secondColor={Colors.amarelo[300]} 
                        thirdColor={Colors.amarelo[300]}/>
                break;
            case CategoriaGastoEnum.ESSENCIAIS:
                color = "verde"
                icone = <PiramideIcon height="35" width="35" 
                        firstColor={Colors.verde[950]}
                        secondColor={Colors.verde[300]} />
                break;
            case CategoriaGastoEnum.EXTRAS:
                    color = "vermelho"
                    icone = <MoedaIcon height="35" width="35" 
                            firstColor={Colors.vermelho[950]}
                            secondColor={Colors.vermelho[300]}/>
                break;
            case CategoriaGastoEnum.MEDICACAO:
                    color = "amarelo"
                    icone = <MedicacaoIcon height="35" width="35" 
                            firstColor={Colors.amarelo[950]}
                            secondColor={Colors.amarelo[300]} 
                            thirdColor={Colors.amarelo[400]}/>
                break;
            default:
                color = "cinza"
                icone = <MoedaIcon height="35" width="35" 
                            firstColor={Colors.cinza[950]}
                            secondColor={Colors.cinza[300]}/>
                break;
        }

        let r = {
            colorName: color,
            icone: (
                <View style={{
                    backgroundColor: oColors[color][300],
                    height: 50,
                    width: 50,
                    alignItems: "center",
                    justifyContent: "center",
                    borderRadius: 45
                }}>
                    {icone}
                </View>
                
            )
        }
        
        return r;
    }
}

function Section({ children, backgroundColor, marginBottom }: any) {

    return (
        <View style={[styles.section, { backgroundColor: backgroundColor, marginBottom: marginBottom }]} >
            {children}
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        padding: 20
    },
    topPage: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
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
