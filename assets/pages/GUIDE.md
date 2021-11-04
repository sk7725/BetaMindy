## The Basics

### Text

Lines with no indicator at the start will be considered normal text until two newlines. Text styling is still the same as Mindustry's with hex color codes or color names in square brackets to set color (e.g ``[#84ff00]`` or ``[accent]``)
```
This is line 1,
This is still line 1.

This is now line 2
[red]this is some red text in line2[] oh it's normal again.
```
### Headers

Headers indicated by `[h]` at the start of the line, any following text will be considered to be header text until two newlines.
```
[h]This is header text,
this is still header text.

This is no longer header text.
```
### Images

Images are indicated by `[i]` at the start of the line, the following text will be considered the name of the image sprite until it meets two newlines or `[t]` , where the following text will be considered to be caption text until it meets two newlines.
 ```
[i]betamindy-sprite-name
-still-a-sprite-name

[i]betamindy-another-sprite-name[t]Fig 1. this is caption text,
this is still caption text.

This is no longer caption text.
 ```

### Notes

Notes are indicated by `[n]` at the start of the line, the following text will be considered the quote until it meets two newlines or `[t]` , where the following text will be considered to be the owner of the quote until it meets two newlines.
 ```
[n]roses are red
life is but a dread
anuke earraped me
and sk is dead

[n]cute skzfowbabaozjwjsdjaieja[t]EyeofDarkness
 ```
## Tables

The start of a table is indicated by `[tb]` at the start of the line. This line should also contain table parameters.
The end of the table is indicated by `[/tb]`.

### Parameters

+ `[c:HEXCOLOR]` sets the color of the table border.
+ `[s:STROKE]` sets the stroke (width) of the table border. (Default: 4)
+ `[pad:PAD]` sets the left and right padding of the text inside the table.(Default: 7.5)
+ `[t:TITLE_HERE]` sets the title of the table.
+ `[col]` enables vertical column borders.
+ `[left]` enables left aligning.
+ `[div:REGEX_HERE]` enables compact table mode. Table columns are separated by the regex supplied.

Tables are separated into two types, expanded(default) or compact.

### Extended Table

This table has no `[div]` parameter. It uses double newlines for columns, and `-`s for rows (the number of `-`s does not matter).
Single newlines, of course, get included in the same cell.

This table supports images and notes. It does not support headers.

<details>
<summary>Example</summary>

```
[tb] [c:ff00ff] [col] [t:Behold]

Photo

[i]router

--------

Name

BasedUser

---------

About

Uh someone i guess
yeah idk

----

Notes

[n]Cute with cat ears. High potential for being turned into a chan.[t]sk7725

--------------

[/tb]
```

</details>

### Compact Table

This table has a `[div]` parameter. It uses `[div:REGEX]`'s regex for columns, and double newlines for rows.
Single newlines, of course, get included in the same cell.

This table does not support any markdowns except colored text.

<details>
<summary>Example</summary>

```
[tb] [c:ff00ff] [col] [t:Behold] [div:\|]

Photo|nope.

Name|BasedUser

About|Uh someone i guess
yeah idk

Notes|Cute with cat ears. High potential for being turned into a chan.
-sk7725

[/tb]
```

</details>