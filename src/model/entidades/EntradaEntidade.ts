import { Column } from "../../decorators/DatabaseDecorators";

export class Entrada {
    
    @Column("ID_ENTRADA", true)
    idEntrada!: number;
    


}