# LLM prompts for stochastic analysis

## DTCM

### Model

Model representing the weather of Belfast.

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

![Diagram of the model](./Model-representing-the-weather-of-Belfast-in-Northern-Ireland.png)

### Queries

```prism
"Rainy"
P<=0.8[("Rainy") & (X ("Rainy"))]
```
```prism
P=?[("Rainy") & (X ("Cloudy"))]
P=?[(X("Sunny")) & (X(X ("Cloudy")))] 
```
```prism
P=?[F<=10 "Sunny"]
P=?[F<=10 "Cloudy"]
P=?[G<=10("Rainy")]
S=?["Cloudy"]
```

### Full prompt

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