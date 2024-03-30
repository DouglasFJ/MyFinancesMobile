import { StyleProp, TextStyle, View, ViewStyle } from "react-native";
import NunitoText from "./NunitoText";


export default function DadosSensiveisText(
    {children, isVisible, style, styleLine}:
    {children:any, isVisible: boolean, style?: StyleProp<TextStyle>, styleLine?:StyleProp<ViewStyle>}
) {
    
    let oStyle: any = style
    let sChildren: string = children;

    let component = isVisible?
    <NunitoText style={style} >{children}</NunitoText>:
    <View style={[{
        width: sChildren.length * (oStyle.fontSize / 2),
        height: 1,
        marginTop: oStyle.fontSize / 2,
        marginBottom: oStyle.fontSize / 2,
        backgroundColor: oStyle.color
    }, styleLine]}></View>

    return(component)
    
}