# Code-Truncater
*Code-Truncater* is a tool that was built to assist in a research task in which the aim is to automatically repair programs with code completion from *CodeGPT*.

The paper that describes that work and presents its results was published in APR 2022: 

[F. Ribeiro, R. Abreu and J. Saraiva, "Framing Program Repair as Code Completion," 2022 IEEE/ACM International Workshop on Automated Program Repair (APR), 2022, pp. 38-45, doi: 10.1145/3524459.3527347.](https://ieeexplore.ieee.org/abstract/document/9809167)

The main purpose of this tool is to analyze a line of *Java* source code and compute adequate column numbers where code can be **truncated** (or **cut**). These column numbers correspond to:
- Textual boundaries of language constructs represented by
AST nodes;
- Camel-case and underscore separation of words according
to *Java* naming conventions.

## Build
To build the `jar` file, run:
```
mvn package
```

which will create an executable package called `code-truncater.jar` with all dependencies and place it in the `target/` directory.

## Usage
To compute column numbers for a line of source code:
```
java -jar code-truncater.jar <java-src-file> <line-nr>
```

However, *Code-truncater* supports other modes of execution. The corresponding switches are given as the first argument:
- `--bulk`: Requires a data file containing space-separated lines with the format:
```
<java-src-file> <line-nr>
```
and a prefix that is prepended to each `<java-src-file>`. Column numbers will be computed for each pair in the data file.

Example usage:
```
java -jar code-truncater.jar --bulk <data-file> <prefix>
```

`<prefix>` is provided separately because `<data-file>` is expected to have file paths that are relative to their main location, which for this study is a publicly available repository for people to be able to reproduce the experiments.

As such, all the paths in `<data-file>` are expected to be relative to `<prefix>`.

More precisely, if there is a `data.txt`:
```
src/main/java/File1.java <line_nr_1>
src/main/java/File2.java <line_nr_2>
...
```

referring to file paths in `/home/user/project-dir/`, the command to execute is:
```
java -jar code-truncater.jar --bulk data.txt /home/user/project-dir/
```

- `--is-str`: Tells if the AST node in a particular line and column is a string literal. Works similar to `--bulk` mode but the data file expects a third field containing the column number to analyze:

```
<java-src-file> <line-nr> <column-nr>
```

Example usage:
```
java -jar code-truncater.jar --is-str <data-file> <prefix>
```

Outputs the `<java-src-file>` if a string literal is present in `<line-nr>` and `<column-nr>`.
Empty, otherwise.

- `--node-type`: Same as `--is-str` but determines the AST node type.
For each line in the data file, outputs that same line plus a fourth field containing the AST node type.

Assuming `data.txt` contains:
```
src/main/java/File1.java 10 4
src/main/java/File1.java 10 7
...
```

and `src/main/java/File1.java` contains:
```
...
10  foo("string");
...
```

and that columns 4 and 7 are characters '(' and 't', respectively, the output will be:
```
src/main/java/File1.java 10 4 MethodCallExpr
src/main/java/File1.java 10 7 StringLiteralExpr 
...
```

Example usage:
```
java -jar code-truncater.jar --node-type <data-file> <prefix>
```
