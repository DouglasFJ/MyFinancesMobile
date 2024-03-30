import { StyleProp, TextStyle, View } from "react-native";
import NunitoText from "./NunitoText";


export default function DadosSensiveisText(
    {children, isVisible, style}:
    {children:any, isVisible: boolean, style?: StyleProp<TextStyle>}
) {
    
    let oStyle: any = style
    let sChildren: string = children;

    let component = isVisible?
    <NunitoText style={style} >{children}</NunitoText>:
    <View style={{
        width: sChildren.length * 10,
        height: 2,
        marginTop: oStyle.fontSize / 2,
        marginBottom: oStyle.fontSize / 2,
        backgroundColor: oStyle.color
    }}></View>

    return(component)
    
}