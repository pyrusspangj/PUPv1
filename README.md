# PUPv1

This is the initial release of PUP. 
The code is sloppy in some parts, but when syntax is correct, so is the result.
I will be working to refactor the code over time.

PUP Syntax:
To use variable values, do so by surrounding the variable name in underscores.

Initialize and edit variables: 'set [variable name] to [value]'
                               'set [variable name] to length of [string or list]' <-- INCLUDE UNDERSCORES FOR LIST
                               'change [variable name] to [value]'
Float: 'set age to 18'
String 'set name to Hunter Szabo'
Boolean: 'set sleeping to false'
Object (Still in development): 'set myobj to new object'
List: 'set mylist to a list of (1, 2, 3, 4, 5)'

Get and set list indices: 'set [variable name] to value at [index] in [list]' <-- DO NOT INCLUDE UNDERSCORES FOR LIST
                          'change value at [index] to [value] in [list]' <-- DO NOT INCLUDE UNDERSCORES FOR LIST

Printing: 'write [content]'

If statements: 'if1 [condition]
                  if2 [condition]
                  end if2
                  # Comment line
                  if2 [condition]
                  end if2
                  else2
                  end else2
                end if1'
                Increment the number by 1 for every 'if', decrement by 1 for ever 'end if'

SINGLE LINE IF-ELSE: '[if statement] > if [condition]
                      <> [else statement]'

Loops: 'loop 3 times
        end loop'
       'loop while [boolean]
        end loop'
       'loop through [list] as [variable name]
        end loop' <-- AGAIN DO NOT USE UNDERSCORES FOR THE LIST

That is all for PUP v1, thank you
