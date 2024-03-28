--
-- Arquivo gerado com SQLiteStudio v3.4.4 em seg mar 25 14:13:20 2024
--
-- Codificação de texto usada: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Tabela: MFTB001_ENTRADA
CREATE TABLE IF NOT EXISTS MFTB001_ENTRADA (
    ID_ENTRADA          INTEGER     PRIMARY KEY AUTOINCREMENT
                                    NOT NULL,
    NO_ENTRADA          TEXT (255)  NOT NULL,
    VR_ENTRADA          REAL (6, 2) NOT NULL,
    DT_CADASTRO_ENTRADA TEXT (30)   NOT NULL,
    CK_ENTRADA_FIXA     INTEGER (1) NOT NULL
                                    CHECK (CK_ENTRADA_FIXA = 0 OR 
                                           CK_ENTRADA_FIXA = 1),
    CK_ENTRADA_VALE     INTEGER     CHECK (CK_ENTRADA_VALE = 0 OR 
                                           CK_ENTRADA_VALE = 1) 
                                    NOT NULL
);


-- Tabela: MFTB002_MES
CREATE TABLE IF NOT EXISTS MFTB002_MES (
    ID_MES INTEGER     PRIMARY KEY AUTOINCREMENT
                       NOT NULL,
    NU_MES INTEGER (2) NOT NULL,
    NU_ANO INTEGER (4) NOT NULL
);


-- Tabela: MFTB003_GASTOS
CREATE TABLE IF NOT EXISTS MFTB003_GASTOS (
    ID_GASTO           INTEGER     PRIMARY KEY AUTOINCREMENT
                                   NOT NULL,
    NO_GASTO           TEXT (255)  NOT NULL,
    VR_GASTO           REAL (6, 2) NOT NULL,
    DT_CADASTRO_GASTO  TEXT (30)   NOT NULL,
    CK_GASTO_FIXO      INTEGER (1) NOT NULL
                                   CHECK (CK_GASTO_FIXO = 0 OR 
                                          CK_GASTO_FIXO = 1),
    CK_PAGO            INTEGER (1) CHECK (CK_PAGO = 0 OR 
                                          CK_PAGO = 1) 
                                   NOT NULL,
    NU_CATEGORIA_GASTO INTEGER (2) CHECK (NU_CATEGORIA_GASTO = 0 OR 
                                          NU_CATEGORIA_GASTO = 1 OR 
                                          NU_CATEGORIA_GASTO = 2 OR 
                                          NU_CATEGORIA_GASTO = 3 OR 
                                          NU_CATEGORIA_GASTO = 4) 
                                   NOT NULL,
    CK_GASTO_VALE      INTEGER     CHECK (CK_GASTO_VALE = 0 OR 
                                          CK_GASTO_VALE = 1) 
                                   NOT NULL
);


-- Tabela: MFTB004_ENTRADA_MES
CREATE TABLE IF NOT EXISTS MFTB004_ENTRADA_MES (
    ID_ENTRADA_MES      INTEGER   PRIMARY KEY AUTOINCREMENT
                                  NOT NULL,
    ID_MES              INTEGER   CONSTRAINT FK_MFTB004_MFTB002 REFERENCES MFTB002_MES (ID_MES) 
                                  NOT NULL,
    ID_ENTRADA          INTEGER   CONSTRAINT FK_MFTB004_MFTB001 REFERENCES MFTB001_ENTRADA (ID_ENTRADA) 
                                  NOT NULL,
    DT_INSERCAO_ENTRADA TEXT (30) NOT NULL
);


-- Tabela: MFTB005_GASTOS_MES
CREATE TABLE IF NOT EXISTS MFTB005_GASTOS_MES (
    ID_GASTO_MES      INTEGER   PRIMARY KEY AUTOINCREMENT
                                NOT NULL,
    ID_MES            INTEGER   CONSTRAINT FK_MFTB005_MFTB002 REFERENCES MFTB002_MES (ID_MES) 
                                NOT NULL,
    ID_GASTO          INTEGER   CONSTRAINT FK_MFTB005_MFTB003 REFERENCES MFTB003_GASTOS (ID_GASTO) 
                                NOT NULL,
    DT_INSERCAO_GASTO TEXT (30) NOT NULL
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
