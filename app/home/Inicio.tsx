import { Button, Text, View } from "react-native";
import Colors from "../../src/ui/Colors";
import { BottomTabScreenProps } from "@react-navigation/bottom-tabs";


export default function InicioScreen({navigation, route}: BottomTabScreenProps<any>) {
    

    return(
        <>
            <Button title="dev helper" onPress={()=>{navigation.navigate("devHelpReact")}}/>
            <View>
                <Text>Tela inicial</Text>
            </View>
        </>
    )

}