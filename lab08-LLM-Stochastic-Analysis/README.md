# Lab 08 - LLM Stochastic Analysis

> Advanced Software Modeling and Design - 2024/25
>
> Author: *Ravaioli Michele*
> E-mail: *michele.ravaioli3@studio.unibo.it*

## Prompt

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

## Results

### GPT-5 Thinking Mini (ChatGPT)

1. true
2. 0.15
3. 0.015
4. 0.44699853156562475
5. 0.1073741824
6. 0.16875

### Gemini 2.5 Pro (Google Gemini)

1. true
2. 0.15
3. 0.015
4. 0.447849690625
5. 0.1073741824
6. 0.16875

### Claude Sonnet 4.5 (Claude)

1. true
2. 0.15
3. 0.015
4. 0.9999990463
5. 0.1073741824
6. 0.230769230769

### Deepseek

1. true
2. 0.105
3. 0.015
4. 0.9999999999999998
5. 0.0
6. 0.2666666666666667