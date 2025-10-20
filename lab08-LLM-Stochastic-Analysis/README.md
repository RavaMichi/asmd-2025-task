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

Lo stesso prompt è stato fornito a quattro LLM differenti: GPT-5 Thinking Mini, Gemini 2.5 Pro, Claude Sonnet 4.5 e Deepseek. Al modello è stata fornita la definizione PRISM e sei proprietà da verificare. Il prompt richiedeva risposte brevi e numerate, senza testo aggiuntivo; inoltre è stato richiesto di spiegare che cosa rappresentino, e quindi se riescano a comprenderne il significato.

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
2. P=?[X ("Cloudy")]
3. P=?[(X("Sunny")) & (X(X ("Cloudy")))] 
4. P=?[F<=10 "Sunny"]
5. P=?[G<=10("Rainy")]
6. S=?["Cloudy"]
   
Think deeply about the problem, understand the model and how to verify it, study the syntax and documentation of PRISM before replying, take your time for the answers. Try to understand what these properties mean and give a textual brief explanation for each of them.
Give a short, brief answer, no other text, only the results in a numbered list. DO NOT USE ANY EXTERNAL TOOL, ONLY YOUR OWN PREDICTIONS OF THE MODEL.

---

Example input:
1. "Sunny"
2. P=?[X ("Rainy")]

Example output (what you should reply):
1. false - "The first state is Sunny"
2. 0.8 - "What's the probability of the second state being Rainy?"
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
### Risposte

**GPT-5 Thinking Mini**

```txt
1. true - "The initial state is Rainy."
2. 0.15 - "One-step probability from Rainy (s=0) to Cloudy (s=2) is 0.15."
3. 0.015 - "Prob(next is Sunny)=0.05 and then Cloudy from Sunny=0.3 → 0.05·0.3=0.015."
4. 0.4469985316 - "Prob of reaching Sunny within 10 steps ≈ 1 − prob(staying in {Rainy,Cloudy} for 10 steps) ≈ 0.447."
5. 0.1073741824 - "Prob all states up to 10 are Rainy = 0.8^10 ≈ 0.1073741824."
6. 0.16875 - "Steady-state (long-run) probability of Cloudy = 27/160 ≈ 0.16875."
```

**Gemini 2.5 Pro**

```txt
1. `true` - "Is the initial state (s=0) 'Rainy'?"
2. `0.15` - "What is the probability that the next state (at time 1) is 'Cloudy' (s=2), starting from the initial state (s=0)?"
3. `0.015` - "What is the probability that the state at time 1 is 'Sunny' (s=1) AND the state at time 2 is 'Cloudy' (s=2)?"
4. `0.44513396925` - "What is the probability of reaching the 'Sunny' state (s=1) at least once within the first 10 steps (i.e., at time $t=0, 1, ..., \text{or } 10$)?"
5. `0.1073741824` - "What is the probability of remaining in the 'Rainy' state (s=0) for all of the first 10 steps (i.e., at time $t=0, 1, ..., \text{and } 10$)?"
6. `0.16875` - "What is the long-run, steady-state probability of being in the 'Cloudy' state (s=2)?"
```

**Claude Sonnet 4.5**

```txt
Looking at this DTMC model, I need to trace through the transition probabilities starting from the initial state s=0 (Rainy).

**Initial state: s=0 (Rainy)**

Let me verify each property:

1. **false** - The initial state is s=0 which is labeled "Rainy", not "Cloudy". The property "Rainy" evaluates to true in state 0, but since we need "Cloudy" to hold initially, this is false.

Wait, let me reconsider property 1. It's asking if "Rainy" holds in the initial state.

1. **true** - The initial state s=0 satisfies the label "Rainy"

2. **0.15** - Probability that the next state is "Cloudy" (s=2) from initial state s=0

3. **0.025** - Probability of next state being "Sunny" (s=1) AND two steps ahead being "Cloudy" (s=2). From s=0: P(s'=1)=0.05, then from s=1: P(s'=2)=0.3, so 0.05 × 0.3 = 0.015. Actually, need to recalculate: 0.05 × 0.3 = 0.015

Let me recalculate: From s=0, prob to s=1 is 0.05. From s=1, prob to s=2 is 0.3. So 0.05 × 0.3 = 0.015

3. **0.015** - Probability of reaching "Sunny" in one step AND "Cloudy" in two steps from initial state

4. **0.3275** - Probability of eventually reaching "Sunny" within 10 steps (requires iterative calculation of reachability)

5. **0** - Probability of staying in "Rainy" for all of the first 10 steps. Since there's 0.8 probability of staying but 0.2 probability of leaving each step, staying for 10 consecutive steps: 0.8^10 ≈ 0.107

5. **0.107** - Probability that "Rainy" holds for all states in paths of length ≤10

6. **0.638** - Steady-state probability of being in "Cloudy" state (requires solving stationary distribution)
```

### Tabella coi risultati


## Analisi risultati

* **Accuratezza generale**: GPT-5 Thinking Mini e Gemini 2.5 Pro forniscono risposte molto vicine al ground truth di PRISM su quasi tutte le proprietà, con differenze minime dovute probabilmente a precisione numerica. Claude e Deepseek presentano deviazioni significative su alcune proprietà (in particolare le proprietà temporali 4 e 6).

* **Proprietà 4 (reachability bounded)**: mentre PRISM, GPT-5 e Gemini sono allineati (∼0.447), Claude e Deepseek riportano valori praticamente unitari per questa probabilità: probabilmente hanno avuto sbagliato nell'interpretazione dell'operatore temporale o nel calcolo iterativo.

* **Proprietà 6 (steady-state)**: GPT-5 e Gemini sono coerenti con la precisione limitata; Claude e Deepseek danno valori più alti, indicando stime errate della distribuzione stazionaria o di interpretazione della query `S=?["Cloudy"]`.

## Conclusione

I LLM più sofisticati (qui GPT-5 e Gemini) riescono a emulare con buona precisione il comportamento di uno model checker per proprietà semplici e a breve orizzonte temporale, ma restano fragili su proprietà temporali più complesse o interpretazioni formali non banali. Claude Sonnet 4.5 e Deepseek mostrano invece difficoltà maggiori, e talvolta forniscono risultati non plausibili rispetto ai risultati di PRISM.

Per usi pratici, i LLM possono essere utili come supporto qualitativo o per verifiche rapide, ma non sostituiscono uno strumento formale come PRISM quando è richiesta certezza numerica.
