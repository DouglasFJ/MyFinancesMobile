import { ReactNode } from "react";
import { StyleProp, StyleSheet, Text, TextStyle } from "react-native";
import Colors from "../ui/Colors";

export default function NunitoText(
    {children, style}: 
    {children?: any, style?: StyleProp<TextStyle>}
    ) {
    
    return(
        <Text style={[fontStyle.font,style]}>
            {children}
        </Text>
    )
}

const fontStyle = StyleSheet.create({
    font: {
        fontFamily: 'Nunito-SemiBold',
        fontWeight: "600",
        color: Colors.cinza[800]
    }
})