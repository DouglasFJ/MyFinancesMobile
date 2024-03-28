export type InitParams = {
    initDatabase: boolean;

    /**
     * se deve reiniciar o banco mesmo se o arquivo já existir
     */
    restartDatabase?: boolean;
};