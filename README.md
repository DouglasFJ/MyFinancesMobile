# MyFinances

Aplicativo Android em Java para controle financeiro offline. Os dados são armazenados em SQLite com Room.

## Funcionalidades

- Início: receitas, despesas, saldo previsto, despesas pendentes e últimas despesas do mês.
- Mês: seleção de mês/ano, histórico mensal, previsão de saldo acumulado e cadastro, edição e exclusão de receitas, despesas e impostos.
- Lançamentos fixos: repetição mensal a partir do mês escolhido. Edições e exclusões afetam esse mês e os seguintes, preservando os anteriores. O pagamento é independente em cada mês.
- Cartões: cadastro, renomeação, exclusão de cartões sem compras e cadastro, edição e exclusão de compras com até 120 parcelas. A fatura pode ser consultada por cartão e mês.
- Faturas: página de consulta por mês e cartão, com total da fatura, valores pagos e pendentes e detalhes de cada compra (valor da parcela, valor total do produto, número da parcela e quantidade de parcelas).
- Backup: exportação de uma cópia SQLite independente pelo seletor de arquivos do Android, incluindo provedores de nuvem instalados.
- Importação: seleção de um backup `.db` do MyFinances na mesma aba Backup, com aviso e confirmação explícita de substituição de todos os dados atuais.

O saldo é **previsto** e acumulado: saldo anterior + receitas do mês − todas as despesas do mês, inclusive impostos e parcelas, independentemente do pagamento. O resultado isolado do mês (receitas − despesas) também aparece nos painéis. Não representa saldo bancário conciliado. Impostos já estão incluídos no total de despesas.

O saldo anterior é calculado em memória a partir de todos os meses anteriores cadastrados, começando em zero no primeiro mês. Ele não é gravado como receita nem como lançamento fixo. Alterar ou excluir um lançamento histórico recalcula os saldos seguintes, inclusive previsões futuras e saldos negativos. Uma fatura é a soma das parcelas do cartão naquele mês; consultá-la não cria outra despesa.

Compras no cartão recebem o **valor total**. As parcelas são distribuídas em meses consecutivos, com eventuais centavos nas primeiras parcelas. Editar uma compra recalcula todas as parcelas e redefine seus pagamentos como pendentes. A compra não deve ser registrada novamente como uma despesa avulsa.

A importação substitui o arquivo inteiro do banco, sem mesclar registros. Após selecionar o arquivo, o usuário precisa confirmar **Apagar dados e importar**. Cancelar a seleção ou a confirmação mantém os dados atuais. É recomendável exportar uma cópia antes de confirmar.

O arquivo selecionado é copiado para uma área temporária e validado (integridade SQLite, versão, estrutura das tabelas e referências entre registros) antes da substituição. Backups da versão 1 são migrados para a versão 2 na cópia temporária. Arquivos corrompidos ou incompatíveis são recusados. A aplicação bloqueia operações durante a importação, fecha as conexões antigas e abre novamente o banco importado. Uma interrupção durante a troca permite recuperar o banco anterior ao reabrir o aplicativo. A cópia de segurança interna usada na troca é removida quando a importação termina com sucesso.

A aba Backup substitui o placeholder da antiga aba Caixa. A sincronização com a nuvem não é automática. O cancelamento do seletor de exportação não exporta nada.

## Organização

`FinanceViewModel` coordena as telas e executa as operações fora da thread principal. `FinanceRepository` define o contrato de armazenamento; `RoomFinanceRepository` implementa esse contrato usando Room e transações para lançamentos e parcelamentos. Uma implementação futura pode substituir o repositório sem incluir consultas SQL nas telas.

O banco está na versão 2. A migração `MIGRATION_1_2` preserva registros existentes e adiciona a identificação de imposto e os limites da recorrência. Não há migração destrutiva.

## Execução e validação

Requisitos: JDK 21, Android SDK 35 e dispositivo/emulador Android 13 (API 33) ou superior. O Gradle usa os critérios em `gradle/gradle-daemon-jvm.properties` para selecionar o JDK 21 instalado. Configure o SDK em `local.properties`.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:lintDebug
```

Os testes cobrem valores em reais, centavos e parcelamento, totais e impostos, pagamentos, recorrência e histórico, cartões e compras, persistência, exportação SQLite, migração da versão 1 e navegação/formulários. Os testes de repositório usam bancos próprios, separados do banco normal do aplicativo.
