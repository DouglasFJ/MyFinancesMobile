import { GestureResponderEvent, StyleProp, TouchableNativeFeedback, View, ViewStyle } from "react-native";

export default function NativeButton(
    {children, styleWrapper, styleView, onPress, feedbackColor}: 
    {children: any, styleWrapper?: StyleProp<ViewStyle>, styleView?: StyleProp<ViewStyle>, onPress: ((event: GestureResponderEvent) => void), feedbackColor: string}
) {
    
    return(
        <View style={[styleWrapper, {overflow: "hidden"}]}>
            <TouchableNativeFeedback 
            background={TouchableNativeFeedback.Ripple(
                feedbackColor,
                false
            )}
            onPress={onPress}>
                <View style={styleView}>
                    {children}
                </View>
            </TouchableNativeFeedback>
        </View>
    )
}