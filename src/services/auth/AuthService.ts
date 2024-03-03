import * as LocalAuth from "expo-local-authentication";

export async function requerirAutenticacao(): Promise<LocalAuth.LocalAuthenticationResult> {
    return LocalAuth.authenticateAsync({
        promptMessage: "Autenticar para acessar o app",
    })
}

export async function dispositivoPossuiAutenticaoLocal(): Promise<boolean> {
    let suporteHardware = await LocalAuth.hasHardwareAsync()
    return suporteHardware?LocalAuth.isEnrolledAsync():false
}

