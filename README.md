# False Positive Test Project

## Overview

This is a **comprehensive test project** containing false positive scenarios for multiple CxQL security queries:

1. **Stored_XSS** - Cross-Site Scripting from database/file sources (3 files, 16 scenarios)
2. **Reflected_XSS** - Cross-Site Scripting from user input (5 files, 50 scenarios)
3. **Unchecked_Input_for_Loop_Condition** - Loop conditions with user input (4 files, 40 scenarios)
4. **Privacy_Violation** - Exposure of personal information (3 files, 40 scenarios)

## 📊 Project Statistics

- **Total Files**: 20 Java files + 2 JSP files + 3 JUnit tests + 3 config files = **28 files**
- **Total Scenarios**: **200+ individual test cases**
- **Coverage**: Web applications, CLI applications, JSP pages, JUnit tests, complex real-world scenarios
- **Complexity Levels**: Simple patterns → Intermediate patterns → Complex real-world applications

## Project Structure

```
FalsePositiveTestProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── checkmarx/
│   │   │           └── falsepositive/
│   │   │               ├── xss/
│   │   │               │   ├── stored/          # Stored XSS false positives
│   │   │               │   └── reflected/       # Reflected XSS false positives
│   │   │               ├── loop/                # Loop condition false positives
│   │   │               ├── privacy/             # Privacy violation false positives
│   │   │               ├── webapp/              # Web application scenarios
│   │   │               └── cli/                 # Command-line scenarios
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml
│   │       └── jsp/                             # JSP files for web scenarios
│   └── test/
│       └── java/                                # Test scenarios
├── pom.xml                                      # Maven build file
└── README.md                                    # This file
```

## False Positive Patterns Covered

### 1. Stored XSS False Positives
- Numeric database fields (Integer, Long, Double)
- Enum values from database
- Entity numeric getters (getId(), getAccountId())
- Pre-sanitized data from database
- Fixed IDs and constants

### 2. Reflected XSS False Positives
- Type conversion (Integer.parseInt, Long.parseLong)
- Number formatting (DecimalFormat, NumberFormat)
- Regex validation with Pattern.matcher()
- Enum validation
- Ternary expressions with numeric operations
- Mathematical operators (+, -, *, /, %)
- Custom escaping/sanitization

### 3. Unchecked Loop Condition False Positives
- Type conversion in loop conditions
- Number formatting in loop conditions
- Regex validation in loop conditions
- Numeric getters in loop conditions
- Ternary expressions in loop conditions
- Mathematical operators in loop conditions
- Enum methods in loop conditions

### 4. Privacy Violation False Positives
- StringBuilder.Append() (intermediate operation, not a sink)
- Constants as parameter keys
- Test files with mock data
- Metadata variables (format patterns, configuration, not actual PII)

## How to Use

### Build the Project
```bash
mvn clean compile
```

### Run CxQL Scan
1. Import this project into Checkmarx
2. Run scan with queries: Stored_XSS, Reflected_XSS, Unchecked_Input_for_Loop_Condition, Privacy_Violation
3. Review findings - all should be false positives

### Expected Results (Before Query Fixes)
- **Stored XSS**: ~16 findings (from 16 scenarios)
- **Reflected XSS**: ~50 findings (from 50 scenarios)
- **Loop Condition**: ~40 findings (from 40 scenarios)
- **Privacy Violation**: ~40 findings (from 40 scenarios)
- **Test Files**: ~33 findings (from JUnit tests - should be excluded)
- **Complex Scenarios**: ~20 findings (from complex web/CLI/JSP files)
- **Total**: ~200 findings (all false positives)

### Expected Results (After Query Fixes)
- **All queries**: 0 findings (100% false positive reduction)
- **Goal**: Achieve 100% precision by eliminating all false positives

## Scenario Categories

### Web Application Scenarios
Located in `src/main/java/com/checkmarx/falsepositive/webapp/` and `src/main/webapp/jsp/`
- Servlet-based scenarios with HttpServletRequest/HttpServletResponse
- JSP files for Light Query testing
- Web MVC controllers

### Command-Line Scenarios
Located in `src/main/java/com/checkmarx/falsepositive/cli/`
- Console applications
- File processing
- Database operations without web context

### Test Scenarios
Located in `src/test/java/`
- JUnit test cases with mock data
- Should NOT be flagged (test context)

## 📂 Complete File List

### Stored XSS Scenarios (3 files, 16 scenarios)
1. `StoredXSS_NumericGetters.java` - Entity numeric getters (5 scenarios)
2. `StoredXSS_EnumValues.java` - Enum values from database (5 scenarios)
3. `StoredXSS_BooleanFields.java` - Boolean fields from database (6 scenarios)

### Reflected XSS Scenarios (5 files, 50 scenarios)
1. `ReflectedXSS_TypeConversion.java` - Type conversion (10 scenarios)
2. `ReflectedXSS_NumberFormatting.java` - Number formatting (10 scenarios)
3. `ReflectedXSS_RegexValidation.java` - Regex validation (10 scenarios)
4. `ReflectedXSS_EnumValidation.java` - Enum validation (10 scenarios)
5. `ReflectedXSS_TernaryMathOperators.java` - Ternary & math operators (10 scenarios)

### Loop Condition Scenarios (4 files, 40 scenarios)
1. `LoopCondition_TypeConversion.java` - Type conversion in loops (10 scenarios)
2. `LoopCondition_NumberFormatting.java` - Number formatting in loops (10 scenarios)
3. `LoopCondition_RegexValidation.java` - Regex validation in loops (10 scenarios)
4. `LoopCondition_TernaryEnum.java` - Ternary & enum in loops (10 scenarios)

### Privacy Violation Scenarios (3 files, 40 scenarios)
1. `PrivacyViolation_Metadata.java` - Metadata variables (20 scenarios)
2. `PrivacyViolation_Constants.java` - Constants as keys (10 scenarios)
3. `PrivacyViolation_StringBuilder.java` - StringBuilder.append() (10 scenarios)

### Web Application Scenarios (2 files)
1. `UserServlet.java` - Basic servlet with multiple patterns
2. `ComplexECommerceServlet.java` - **Complex e-commerce application** (combines all patterns)

### Command-Line Scenarios (2 files)
1. `DataProcessor.java` - Basic CLI application
2. `ComplexReportGenerator.java` - **Complex report generator** (combines all patterns)

### JSP Scenarios (2 files)
1. `user-display.jsp` - Basic JSP with false positive patterns
2. `complex-dashboard.jsp` - **Complex dashboard** (combines all patterns)

### JUnit Test Files (3 files, 33 test scenarios)
1. `XSSFalsePositiveTest.java` - XSS test scenarios (10 tests)
2. `LoopConditionFalsePositiveTest.java` - Loop test scenarios (8 tests)
3. `PrivacyViolationFalsePositiveTest.java` - Privacy test scenarios (15 tests)

## 🎯 Validation Files (NEW!)

### Purpose
The validation files contain **BOTH good and bad findings** to ensure query fixes:
1. ✅ Eliminate false positives (GOOD findings should NOT be flagged)
2. ✅ Preserve true positive detection (BAD findings SHOULD be flagged)

### Validation Files
Located in `src/main/java/com/checkmarx/validation/`

| File | Good (FP) | Bad (TP) | Total |
|------|-----------|----------|-------|
| `xss/stored/StoredXSS_Validation.java` | 5 | 8 | 13 |
| `xss/reflected/ReflectedXSS_Validation.java` | 7 | 8 | 15 |
| `loop/LoopCondition_Validation.java` | 6 | 7 | 13 |
| `privacy/PrivacyViolation_Validation.java` | 7 | 12 | 19 |
| **TOTAL** | **25** | **35** | **60** |

### Expected Results

**Before Query Fixes:**
- Total Findings: 60 (25 false positives + 35 true positives)
- Precision: 58.3%
- Recall: 100%

**After Query Fixes:**
- Total Findings: 35 (0 false positives + 35 true positives)
- Precision: 100%
- Recall: 100%

See **VALIDATION_BENCHMARK.md** for detailed metrics and success criteria.

## Notes

- All scenarios are designed to be flagged by CxQL's current logic
- False positive scenarios demonstrate patterns that need query fixes
- True positive scenarios ensure query fixes don't break security coverage
- Each file includes detailed comments explaining why findings are good or bad

## References

- Bug 300145: StringBuilder.append() false positive
- Bug 12908: Constants as keys false positive
- Bug 228125: Test files false positive
- Bug 301872: Validation property paths false positive
- CWE-79: Cross-Site Scripting
- CWE-606: Unchecked Input for Loop Condition
- CWE-359: Exposure of Private Personal Information

