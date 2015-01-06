Generate random JSON documents from a template. Template itself is a
JSON document.

## Template 

```
 "field" : "value",
```
"value" is the value of "field" in output.

```
 "field" : {"$str": { "minlength":2,"maxlength":64 } }
```
Generates a string value for "field" with length 2-64

```
 "field" : { "$choose" : [ 1,2,3,4,5,6,7 ] }
```
"field" value is chosen randomly from one of the values in the array.

```
 "field" : { "$optional" : { "percent":50, 
       "value": { "$choose" : [ "true","false" ] }}}
```
"field" is optional, exists in the output document with 50% probability. If it exists, its value is a random selection between "true" and "false".

```
 "field" : { "$date" : { "min":"2000/01/01", "max":"2015/31/12"} }
```
"field" is a random date between 2000/01/01 and 2015/31/12

```
 "field" : { "$date" : { "fwd":"10d", "bck":"1m"} }
```
"field" is a random date between 1 month back and 10 days forward from now.

```
 "field" : "$int"
````
"field" is a random integer

```
"state" : { "$propfile" : { "file":"adr.properties",
           "choose":"stateAbbreviations" } }
```
"state" is randomly initialized from the property 
"stateAbbreviations", which is a comma or space delimited 
string, from the file "adr.properties".

```
 "arr": { "$array" : { "element" : { 
          "fld1":{"$str":{}},
          "fld2":2,
          "fld3":{"$date": { "fwd":"10d","bck":"1d"}}
      } } }
```
"arr" is a random array, whose elements are "fld1", a random string, 
"fld2" whose value is 2, and "fld3", a random date.


## Extending

To add new generation commands:

 * Implement the Generator interface. The generate() function gets 
   an instance of the Json node containing either the parameters 
   to the generator, or the generator node itself.

```
  "field" : $str,
```
   In this case, the generate function gets a TextNode with value "$str".

```
  "field": {"$str": { "minlength":1 } }
```
   In this case, the generate function gets an ObjectNode with value
   { "minlength":1 }

 * Register an instance of the new class to 
   GeneratorRegistry.getDefaultInstance


# License

The license of lightblue is [GPLv3](https://www.gnu.org/licenses/gpl.html).  See LICENSE in root of project for the full text.

