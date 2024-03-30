import { StyleProp, View, ViewStyle } from "react-native";
import { CategoriaGastoEnum } from "../constantes/CategoriaGastoEnum";
import NunitoText from "./NunitoText";
import DadosSensiveisText from "./DadosSensiveisText";
import { maskBRL } from "../utils/maskUtils";
import InvestimentoIcon from "../ui/icons/InvestimentoIcon";
import Colors from "../ui/Colors";
import AlimentacaoIcon from "../ui/icons/AlimentacaoIcon";
import PiramideIcon from "../ui/icons/PiramideIcon";
import MoedaIcon from "../ui/icons/MoedaIcon";
import MedicacaoIcon from "../ui/icons/MedicacaoIcon";

const oColors: any = Colors;

export default function Gasto(
    {styleView, sizeIcon, isVisible, title, valor, categoria}:
    {styleView?: StyleProp<ViewStyle>,sizeIcon: number, isVisible:boolean, title: string, valor:number, categoria?: CategoriaGastoEnum}
) {
    
    let {colorName, icone} = decidirIconeCor(categoria, sizeIcon);

    return (
        <View style={[{
            flexDirection: "row",
            overflow: "hidden",
            alignItems: "center",
        }, styleView]}>
            {icone}
            <View style={{marginLeft:10}}>
                <NunitoText style={{
                    color: oColors[colorName][950],
                    fontSize: sizeIcon/4,
                    fontWeight: "700"
                }}>{title}</NunitoText>
                <DadosSensiveisText style={{
                    color: oColors[colorName][700],
                    fontSize: sizeIcon/5
                }} isVisible={isVisible}>
                    {maskBRL(valor)}
                </DadosSensiveisText>
            </View>
        </View>
    )

    function decidirIconeCor(categoriaGasto?: CategoriaGastoEnum, sizeIcon: number = 50) {
        let color;
        let icone;

        switch (categoriaGasto) {
            case CategoriaGastoEnum.INVESTIMENTO:
                color = "verde"
                icone = <InvestimentoIcon height={(sizeIcon - 15).toString()} width={(sizeIcon - 15).toString()} 
                        firstColor={Colors.verde[950]}
                        secondColor={Colors.verde[300]} 
                        thirdColor={Colors.verde[300]}/>
                break;
            case CategoriaGastoEnum.ALIMENTACAO:
                color = "amarelo"
                icone = <AlimentacaoIcon height={(sizeIcon - 15).toString()} width={(sizeIcon - 15).toString()} 
                        firstColor={Colors.amarelo[950]}
                        secondColor={Colors.amarelo[300]} 
                        thirdColor={Colors.amarelo[300]}/>
                break;
            case CategoriaGastoEnum.ESSENCIAIS:
                color = "verde"
                icone = <PiramideIcon height={(sizeIcon - 15).toString()} width={(sizeIcon - 15).toString()} 
                        firstColor={Colors.verde[950]}
                        secondColor={Colors.verde[300]} />
                break;
            case CategoriaGastoEnum.EXTRAS:
                    color = "vermelho"
                    icone = <MoedaIcon height={(sizeIcon - 15).toString()} width={(sizeIcon - 15).toString()} 
                            firstColor={Colors.vermelho[950]}
                            secondColor={Colors.vermelho[300]}/>
                break;
            case CategoriaGastoEnum.MEDICACAO:
                    color = "amarelo"
                    icone = <MedicacaoIcon height={(sizeIcon - 15).toString()} width={(sizeIcon - 15).toString()} 
                            firstColor={Colors.amarelo[950]}
                            secondColor={Colors.amarelo[300]} 
                            thirdColor={Colors.amarelo[400]}/>
                break;
            default:
                color = "cinza"
                icone = <MoedaIcon height={(sizeIcon - 15).toString()} width={(sizeIcon - 15).toString()} 
                            firstColor={Colors.cinza[950]}
                            secondColor={Colors.cinza[300]}/>
                break;
        }

        let r = {
            colorName: color,
            icone: (
                <View style={{
                    backgroundColor: oColors[color][300],
                    height: sizeIcon,
                    width: sizeIcon,
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