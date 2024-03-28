// import { Column, Table } from "../../decorators/DatabaseDecorators";
import CacheEntity from "../../cache/CacheEntity";

// @Table("MFTB001_ENTRADA")
export class Entrada {
    
    // @Column("ID_ENTRADA", true)
    idEntrada!: number;
    
    // @Column("NO_ENTRADA")
    noEntrada!: string;

    // @Column("VR_ENTRADA")
    vrEntrada!: number;

    // @Column("DT_CADASTRO_ENTRADA")
    dtCadastro!: Date;

    // @Column("CK_ENTRADA_FIXA")
    ckEntradaFixa!: number;

    /**
     * Indica se a entrada é um vale alimentação
     */
    // @Column("CK_ENTRADA_VALE")
    ckEntradaVale!: number;
}