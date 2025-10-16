# Lab 08 - LLM Stochastic Analysis

> Advanced Software Modeling and Design - 2024/25
>
> Author: *Ravaioli Michele*
> E-mail: *[michele.ravaioli3@studio.unibo.it](mailto:michele.ravaioli3@studio.unibo.it)*

## Abstract

Lo scopo di questo task è indagare sulle potenzialità dei LLM nel supportare il model checking di modelli stocastici. Per farlo si usa PRISM come ground truth: i modelli e le proprietà da verificare sono espressi nella sintassi di PRISM e i risultati ottenuti dallo strumento vengono confrontati con le risposte fornite da diversi LLM.

## Definizione del modello

Il modello scelto è semplice e rappresenta (in modo assai semplificato) il meteo di Belfast. È composto da tre stati e nove transizioni.

![Diagram of the model](./Model-representing-the-weather-of-Belfast-in-Northern-Ireland.png)

Nella sintassi di PRISM il modello è definito come segue:

```prism
dtmc

module msg
  s : [0..2] init 0;
  
  [] s=0 -> 0.8  : (s'=0)
          + 0.05 : (s'=1)
          + 0.15 : (s'=2);
  [] s=1 -> 0.2  : (s'=1) 
          + 0.5  : (s'=0) 
          + 0.3  : (s'=2);
  [] s=2 -> 0.2  : (s'=2)
          + 0.7  : (s'=0)
          + 0.1  : (s'=1);

endmodule

label "Rainy"  = s=0;
label "Sunny"  = s=1;
label "Cloudy" = s=2;
```

## Prompt

Lo stesso prompt è stato fornito a quattro LLM differenti: GPT-5 Thinking Mini, Gemini 2.5 Pro, Claude Sonnet 4.5 e Deepseek. Al modello è stata fornita la definizione PRISM e sei proprietà da verificare. Il prompt richiedeva risposte brevi e numerate, senza testo aggiuntivo.

Proprietà verificate (ordine nel prompt):

1. `"Rainy"` (proposizione su stato iniziale)
2. `P=?[ ("Rainy") & (X ("Cloudy")) ]`
3. `P=?[ (X("Sunny")) & (X(X ("Cloudy"))) ]`
4. `P=?[ F<=10 "Sunny" ]`
5. `P=?[ G<=10 ("Rainy") ]`
6. `S=?["Cloudy"]`

Il prompt completo è il seguente:

```txt
Try to understand the following model, defined using PRISM model checker specific syntax:

dtmc
module msg
  s : [0..2] init 0;
  [] s=0 -> 0.8  : (s'=0)
          + 0.05 : (s'=1)
          + 0.15 : (s'=2);
  [] s=1 -> 0.2  : (s'=1) 
          + 0.5  : (s'=0) 
          + 0.3  : (s'=2);
  [] s=2 -> 0.2  : (s'=2)
          + 0.7  : (s'=0)
          + 0.1  : (s'=1);
endmodule

label "Rainy"  = s=0;
label "Sunny"  = s=1;
label "Cloudy" = s=2;

Then, using this model, verify the following properties and return in a list the results of the verification:

1. "Rainy"
2. P=?[("Rainy") & (X ("Cloudy"))]
3. P=?[(X("Sunny")) & (X(X ("Cloudy")))] 
4. P=?[F<=10 "Sunny"]
5. P=?[G<=10("Rainy")]
6. S=?["Cloudy"]
   
Think deeply about the problem, understand the model and how to verify it, study the syntax and documentation of PRISM before replying, take your time for the answers.
Give a short, brief answer, no other text, only the results in a numbered list. DO NOT USE ANY EXTERNAL TOOL, ONLY YOUR OWN PREDICTIONS OF THE MODEL.

Example input:
1. "Sunny"
2. P=?[("Rainy") & (X ("Rainy"))]

Example output (what you should reply):
1. false
2. 0.8
```

## Risultati

Di seguito i risultati ottenuti con PRISM (ground truth) e le risposte dei quattro LLM.

| Proprietà                          |       PRISM | GPT-5 Thinking Mini | Gemini 2.5 Pro | Claude Sonnet 4.5 |           Deepseek |
| ---------------------------------- | ----------: | ------------------: | -------------: | ----------------: | -----------------: |
| "Rainy"                         |        true |                true |           true |              true |               true |
| P=?["Rainy" & X "Cloudy"]       |        0.15 |                0.15 |           0.15 |              0.15 |              0.105 |
| P=?[ X "Sunny" & X X "Cloudy" ] |       0.015 |               0.015 |          0.015 |             0.015 |              0.015 |
| P=?[ F<=10 "Sunny" ]            | 0.446998531 | 0.44699853156562475 | 0.447849690625 |      0.9999990463 | 0.9999999999999998 |
| P=?[ G<=10 "Rainy" ]            | 0.107374182 |        0.1073741824 |   0.1073741824 |      0.1073741824 |                0.0 |
| S=?["Cloudy"]                   | 0.168750107 |             0.16875 |        0.16875 |    0.230769230769 | 0.2666666666666667 |

## Analisi risultati

* **Accuratezza generale**: GPT-5 Thinking Mini e Gemini 2.5 Pro forniscono risposte molto vicine al ground truth di PRISM su quasi tutte le proprietà, con differenze minime dovute probabilmente a precisione numerica. Claude e Deepseek presentano deviazioni significative su alcune proprietà (in particolare le proprietà temporali 4 e 6).

* **Proprietà 4 (reachability bounded)**: mentre PRISM, GPT-5 e Gemini sono allineati (∼0.447), Claude e Deepseek riportano valori praticamente unitari per questa probabilità: probabilmente hanno avuto sbagliato nell'interpretazione dell'operatore temporale o nel calcolo iterativo.

* **Proprietà 6 (steady-state)**: GPT-5 e Gemini sono coerenti con la precisione limitata; Claude e Deepseek danno valori più alti, indicando stime errate della distribuzione stazionaria o di interpretazione della query `S=?["Cloudy"]`.

## Conclusione

I LLM più sofisticati (qui GPT-5 e Gemini) riescono a emulare con buona precisione il comportamento di uno model checker per proprietà semplici e a breve orizzonte temporale, ma restano fragili su proprietà temporali più complesse o interpretazioni formali non banali. Claude Sonnet 4.5 e Deepseek mostrano invece difficoltà maggiori, e talvolta forniscono risultati non plausibili rispetto ai risultati di PRISM.

Per usi pratici, i LLM possono essere utili come supporto qualitativo o per verifiche rapide, ma non sostituiscono uno strumento formale come PRISM quando è richiesta certezza numerica.
