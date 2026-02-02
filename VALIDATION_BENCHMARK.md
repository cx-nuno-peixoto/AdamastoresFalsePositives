# Validation Benchmark - Good vs Bad Findings

## 🎯 Purpose

This benchmark validates that CxQL query fixes:
1. ✅ **Eliminate false positives (BAD)** - Safe code that should NOT be flagged
2. ✅ **Preserve true positives (GOOD)** - Real vulnerabilities that SHOULD be flagged

**Terminology:**
- **BAD = FALSE POSITIVE (FP)** = Safe code incorrectly flagged → Query fixes should ELIMINATE these
- **GOOD = TRUE POSITIVE (TP)** = Real vulnerabilities correctly flagged → Query fixes should PRESERVE these

## 📊 Validation Files - Java

| File | BAD (FP) | GOOD (TP) | Total |
|------|----------|-----------|-------|
| `StoredXSS_Validation.java` | 7 | 12 | 19 |
| `ReflectedXSS_Validation.java` | 9 | 13 | 22 |
| `LoopCondition_Validation.java` | 8 | 11 | 19 |
| `PrivacyViolation_Validation.java` | 9 | 17 | 26 |
| **Java TOTAL** | **33** | **53** | **86** |

## 📊 Validation Files - C#

| File | BAD (FP) | GOOD (TP) | Total |
|------|----------|-----------|-------|
| `StoredXSS_Validation.cs` | 4 | 9 | 13 |
| `ReflectedXSS_Validation.cs` | 5 | 8 | 13 |
| `LoopCondition_Validation.cs` | 4 | 5 | 9 |
| `PrivacyViolation_Validation.cs` | 5 | 8 | 13 |
| **C# TOTAL** | **18** | **30** | **48** |

## 📈 Expected Scan Results

### Before Query Fixes (Baseline) - Java

| Query | BAD (FP) | GOOD (TP) | Total Findings | Precision |
|-------|----------|-----------|----------------|-----------|
| Stored_XSS | 7 | 12 | 19 | 63.2% |
| Reflected_XSS | 9 | 13 | 22 | 59.1% |
| Unchecked_Input_for_Loop_Condition | 8 | 11 | 19 | 57.9% |
| Privacy_Violation | 9 | 17 | 26 | 65.4% |
| **TOTAL** | **33** | **53** | **86** | **61.6%** |

**Baseline Metrics:**
- **Precision**: 61.6% (53 true positives / 86 total findings)
- **False Positive Rate**: 38.4% (33 false positives / 86 total findings)
- **Recall**: 100% (all 53 vulnerabilities detected)

### After Query Fixes (Target) - Java

| Query | BAD (FP) | GOOD (TP) | Total Findings | Precision |
|-------|----------|-----------|----------------|-----------|
| Stored_XSS | 0 | 12 | 12 | 100% |
| Reflected_XSS | 0 | 13 | 13 | 100% |
| Unchecked_Input_for_Loop_Condition | 0 | 11 | 11 | 100% |
| Privacy_Violation | 0 | 17 | 17 | 100% |
| **TOTAL** | **0** | **53** | **53** | **100%** |

**Target Metrics:**
- **Precision**: 100% (53 true positives / 53 total findings)
- **False Positive Rate**: 0% (0 false positives / 53 total findings)
- **Recall**: 100% (all 53 vulnerabilities still detected)

## 🎯 Success Criteria

### ✅ Query Fixes are Successful if:

1. **Precision Improvement**: 61.6% → 100% (+38.4%)
2. **False Positive Elimination**: 33 → 0 (100% reduction) for Java
3. **Recall Maintained**: 100% → 100% (no regression)
4. **True Positive Detection**: All 53 GOOD findings still flagged

### ❌ Query Fixes Failed if:

1. **Any BAD finding is still flagged** (false positive not eliminated)
2. **Any GOOD finding is not flagged** (true positive lost - recall regression)
3. **Precision < 100%** (still have false positives)
4. **Recall < 100%** (lost security coverage)

## 📋 Detailed Breakdown - Java

### Stored XSS Validation (Java)

**BAD (FP) - Should NOT be flagged after fix:**
1. `badNumericId()` - Numeric getter from database
2. `badLoginCount()` - Integer getter from database
3. `badBooleanField()` - Boolean getter from database
4. `badNumericLoop()` - Numeric ID in loop
5. `badMultipleNumeric()` - Multiple numeric fields
6. `badNumericFromSession()` - Numeric from session
7. `badEnumFromDatabase()` - Enum value (ternary with literals)

**GOOD (TP) - SHOULD be flagged after fix:**
1. `goodUserName()` - String field from database
2. `goodUserEmail()` - Email field from database
3. `goodStringLoop()` - String field in loop
4. `goodDirectGetter()` - Direct getter in output
5. `goodMultipleStrings()` - Multiple string fields
6. `goodHtmlAttribute()` - String in HTML attribute
7. `goodJavaScript()` - String in JavaScript
8. `goodSessionData()` - Session attribute (stored XSS)
9. `goodCookieData()` - Cookie value
10. `goodCSSContext()` - CSS injection
11. `goodEventHandler()` - Event handler XSS
12. `mixedGoodAndBad()` - Mixed scenario

### Reflected XSS Validation (Java)

**BAD (FP) - Should NOT be flagged after fix:**
1. `badIntParse()` - Type conversion to int
2. `badLongParse()` - Type conversion to long
3. `badNumberFormat()` - Number formatting
4. `badRegexValidation()` - Regex validation (strict format)
5. `badEnumValidation()` - Enum validation
6. `badTernary()` - Ternary with numeric
7. `badMathOperators()` - Math operators
8. `badBooleanParse()` - Boolean conversion
9. `badUuidValidation()` - UUID validation

**GOOD (TP) - SHOULD be flagged after fix:**
1. `goodDirectOutput()` - Direct output without validation
2. `goodStringConcat()` - String concatenation
3. `goodHtmlAttribute()` - HTML attribute without encoding
4. `goodJavaScript()` - JavaScript without encoding
5. `goodUrlParameter()` - URL parameter without encoding
6. `goodMultipleParams()` - Multiple parameters
7. `goodRegexButUnsafe()` - Regex validation but unsafe output
8. `goodHeaderReflection()` - HTTP header value
9. `goodCookieReflection()` - Cookie value
10. `goodPathInfoReflection()` - Path info
11. `goodQueryStringReflection()` - Query string
12. `goodJsonContext()` - JSON context injection
13. `mixedGoodAndBad()` - Mixed scenario

### Loop Condition Validation (Java)

**BAD (FP) - Should NOT be flagged after fix:**
1. `badBoundedMathMin()` - Math.min(limit, 100) - max 100 iterations
2. `badBoundedMathMinMax()` - Math.max + Math.min - between 0 and 50
3. `badTernaryBounded()` - Ternary that LIMITS value (max 50)
4. `badTernaryBothBounded()` - Both branches bounded
5. `badEnumLoop()` - Enum with finite values (max 100)
6. `badRegexBoundedDigits()` - Regex `^[0-9]{1,2}$` (max 99)
7. `badConstantBound()` - Hardcoded MAX_ITERATIONS
8. `badIfGuardBounded()` - Explicit if-guard with bound

**GOOD (TP) - SHOULD be flagged after fix:**
1. `goodUnboundedIntParse()` - Integer.parseInt() without bounds → DoS
2. `goodRegexUnbounded()` - Regex `^[0-9]+$` validates format, not VALUE
3. `goodTernaryMultiplication()` - Ternary with `base * 2` → can be huge
4. `goodMathUnbounded()` - Addition doesn't bound (`value + 100`)
5. `goodWhileUnbounded()` - While loop without bounds
6. `goodDoWhileUnbounded()` - Do-while without bounds
7. `goodNestedUnbounded()` - Nested loops (exponential)
8. `goodIntegerOverflow()` - Overflow scenario (`value * 2`)
9. `goodModuloUnbounded()` - Modulo allows large values (`% 1000000`)
10. `goodAbsoluteValue()` - Math.abs() doesn't bound
11. `mixedBoundedAndUnbounded()` - Mixed scenario

### Privacy Violation Validation (Java)

**BAD (FP) - Should NOT be flagged after fix:**
1. `badConstantAsKey()` - Constant as parameter name
2. `badMetadataFormat()` - Metadata format pattern
3. `badMetadataConfig()` - Metadata configuration
4. `badStringBuilderNoOutput()` - StringBuilder without output
5. `badValidationMessage()` - Validation message (field name)
6. `badAccountId()` - Account ID (numeric)
7. `badPhoneFormat()` - Phone format pattern
8. `badErrorFieldName()` - Error field name (not value)
9. `badMaskedValue()` - Properly masked SSN (last 4 only)

**GOOD (TP) - SHOULD be flagged after fix:**
1. `goodPasswordOutput()` - Actual password to response
2. `goodSSNOutput()` - SSN to response
3. `goodCreditCardOutput()` - Credit card to response
4. `goodPasswordLogging()` - Password logged (System.out)
5. `goodCredentialsOutput()` - Credentials to response
6. `goodSecretOutput()` - Secret to response
7. `goodAuthTokenOutput()` - Auth token to response
8. `goodDOBOutput()` - DOB to response
9. `goodPassportOutput()` - Passport to response
10. `goodSocialSecurityOutput()` - Social Security to response
11. `goodStringBuilderWithOutput()` - StringBuilder WITH output
12. `goodExceptionWithPII()` - PII in exception message
13. `goodLoggingSystemErr()` - PII to stderr
14. `goodMultiplePII()` - Multiple PII fields
15. `goodMedicalRecord()` - Medical record number
16. `goodDriversLicense()` - Driver's license
17. `mixedGoodAndBad()` - Mixed scenario

## 🚀 How to Use This Benchmark

### Step 1: Baseline Scan (Before Fixes)
```bash
# Scan the validation files
# Expected Java: 86 findings (33 FP + 53 TP)
# Precision: 61.6%
```

### Step 2: Implement Query Fixes
Based on the patterns in BAD findings, implement CxQL fixes to eliminate false positives.

### Step 3: Validation Scan (After Fixes)
```bash
# Re-scan the validation files
# Expected Java: 53 findings (0 FP + 53 TP)
# Precision: 100%
```

### Step 4: Verify Results

- ✅ All BAD findings eliminated (0 false positives)
- ✅ All GOOD findings preserved (53 true positives)
- ✅ Precision improved from 61.6% to 100%
- ✅ Recall maintained at 100%

## 📊 Metrics to Track - Java

| Metric | Before Fixes | After Fixes | Improvement |
|--------|--------------|-------------|-------------|
| **Total Findings** | 86 | 53 | -38.4% |
| **True Positives** | 53 | 53 | 0% (maintained) |
| **False Positives** | 33 | 0 | -100% |
| **Precision** | 61.6% | 100% | +38.4% |
| **Recall** | 100% | 100% | 0% (maintained) |
| **F1 Score** | 76.2% | 100% | +23.8% |

## 📂 File Structure

```
FalsePositiveTestProject/
├── src/main/java/com/checkmarx/validation/
│   ├── xss/
│   │   ├── stored/StoredXSS_Validation.java
│   │   └── reflected/ReflectedXSS_Validation.java
│   ├── loop/LoopCondition_Validation.java
│   └── privacy/PrivacyViolation_Validation.java
└── src/main/csharp/Validation/
    ├── XSS/
    │   ├── StoredXSS_Validation.cs
    │   └── ReflectedXSS_Validation.cs
    ├── Loop/LoopCondition_Validation.cs
    └── Privacy/PrivacyViolation_Validation.cs
```

---

**Created**: 2026-01-29
**Updated**: 2026-01-30
**Purpose**: Validate CxQL query fixes maintain security coverage while eliminating false positives

