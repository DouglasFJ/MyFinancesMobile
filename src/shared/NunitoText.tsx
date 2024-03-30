import { ReactNode } from "react";
import { StyleProp, StyleSheet, Text, TextStyle } from "react-native";
import Colors from "../ui/Colors";

export default function NunitoText(
    {children, style}: 
    {children?: any, style?: StyleProp<TextStyle>}
    ) {

    let fontName = 'Nunito-SemiBold';

    let oStyle: any = style;

    if (oStyle?.fontWeight) {
        switch (oStyle.fontWeight) {
            case "100":
                fontName = "Nunito-ExtraLight"
                break;
            case "200":
                fontName = "Nunito-Light"
                break;
            case "300":
                fontName = "Nunito-Regular"
                break;
            case "400":
                fontName = "Nunito-Medium"
                break;
            case "500":
                fontName = "Nunito-SemiBold"
                break;
            case "600":
                fontName = "Nunito-Bold"
                break;
            case "700":
                fontName = "Nunito-ExtraBold"
                break;
            case "800":
                fontName = "Nunito-Black"
                break;
            case "900":
                fontName = "Nunito-Black"
                break;
            default:
                break;
        }
    }

    return(
        <Text style={[{fontFamily: fontName},style]}>
            {children}
        </Text>
    )
}
