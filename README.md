# Code-Truncater
*Code-Truncater* is a tool that was built to assist in a research task in which the aim is to automatically repair programs with code completion from *CodeGPT*.

The paper that describes that work and presents its results was published in APR 2022: 

[F. Ribeiro, R. Abreu and J. Saraiva, "Framing Program Repair as Code Completion," 2022 IEEE/ACM International Workshop on Automated Program Repair (APR), 2022, pp. 38-45, doi: 10.1145/3524459.3527347.](https://ieeexplore.ieee.org/abstract/document/9809167)

The main purpose of this tool is to analyze a line of *Java* source code and compute adequate column numbers where code can be **truncated** (or **cut**). These column numbers correspond to:
- Textual boundaries of language constructs represented by
AST nodes;
- Camel-case and underscore separation of words according
to *Java* naming conventions.
