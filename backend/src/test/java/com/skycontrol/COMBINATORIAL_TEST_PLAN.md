# Combinatorial Testing Plan
## SkyControl Pro - Drone Fleet Management System

### Document Information
- **Project**: SkyControl Pro
- **Version**: 1.0
- **Date**: May 2026
- **Author**: Ryan
- **Course**: SEIS 739

---

## 1. Introduction

This document describes the combinatorial testing strategy for the SkyControl Pro drone fleet management system. Combinatorial testing helps identify defects caused by interactions between different input parameters.

### 1.1 Purpose
To systematically test interactions between multiple input parameters in the drone management system, ensuring robust behavior across different combinations of inputs.

### 1.2 Scope
This plan covers combinatorial testing for:
- Drone creation with multiple parameters
- Mission assignment with drone and pilot combinations
- Status transition scenarios

---

## 2. Test Case 1: Drone Creation Parameters

### 2.1 Parameters and Values

| Parameter | Values | Count |
|-----------|--------|-------|
| SerialNumber | Valid, Empty, Duplicate | 3 |
| Model | Valid, Empty, Special Characters | 3 |
| Manufacturer | Valid, Empty, Number | 3 |
| Status | AVAILABLE, MAINTENANCE, OFFLINE | 3 |

**Total Possible Combinations**: 3 × 3 × 3 × 3 = 81

### 2.2 Pairwise (2-way) Coverage

Using pairwise testing to reduce from 81 to approximately 9-12 test cases while maintaining 100% 2-way coverage.

#### Test Cases (2-way covering array):

| Test | SerialNumber | Model | Manufacturer | Status | Expected Result |
|------|--------------|-------|--------------|--------|-----------------|
| 1 | Valid | Valid | Valid | AVAILABLE | Success - Drone created |
| 2 | Valid | Empty | Empty | MAINTENANCE | Fail - Validation error |
| 3 | Valid | Special Chars | Number | OFFLINE | Success - Accepts input |
| 4 | Empty | Valid | Valid | AVAILABLE | Fail - Serial required |
| 5 | Empty | Empty | Number | MAINTENANCE | Fail - Multiple errors |
| 6 | Empty | Special Chars | Empty | OFFLINE | Fail - Multiple errors |
| 7 | Duplicate | Valid | Empty | AVAILABLE | Fail - Duplicate error |
| 8 | Duplicate | Empty | Valid | MAINTENANCE | Fail - Duplicate + validation |
| 9 | Duplicate | Special Chars | Number | OFFLINE | Fail - Duplicate error |

### 2.3 Coverage Matrix

```
SerialNumber × Model Coverage:
✓ (Valid, Valid)
✓ (Valid, Empty)
✓ (Valid, Special Chars)
✓ (Empty, Valid)
✓ (Empty, Empty)
✓ (Empty, Special Chars)
✓ (Duplicate, Valid)
✓ (Duplicate, Empty)
✓ (Duplicate, Special Chars)

All 9 combinations covered ✓
```

---

## 3. Test Case 2: Mission Assignment

### 3.1 Parameters and Values

| Parameter | Values | Count |
|-----------|--------|-------|
| Drone Status | AVAILABLE, IN_FLIGHT, MAINTENANCE, OFFLINE | 4 |
| Pilot Status | ACTIVE, INACTIVE, ON_LEAVE | 3 |
| Mission Type | SURVEILLANCE, DELIVERY, INSPECTION | 3 |

**Total Possible Combinations**: 4 × 3 × 3 = 36

### 3.2 Pairwise Test Cases

| Test | Drone Status | Pilot Status | Mission Type | Expected Result |
|------|--------------|--------------|--------------|-----------------|
| 1 | AVAILABLE | ACTIVE | SURVEILLANCE | Success |
| 2 | AVAILABLE | INACTIVE | DELIVERY | Success (Warning) |
| 3 | AVAILABLE | ON_LEAVE | INSPECTION | Success (Warning) |
| 4 | IN_FLIGHT | ACTIVE | DELIVERY | Fail - Drone busy |
| 5 | IN_FLIGHT | INACTIVE | INSPECTION | Fail - Multiple issues |
| 6 | IN_FLIGHT | ON_LEAVE | SURVEILLANCE | Fail - Multiple issues |
| 7 | MAINTENANCE | ACTIVE | INSPECTION | Fail - Drone unavailable |
| 8 | MAINTENANCE | INACTIVE | SURVEILLANCE | Fail - Multiple issues |
| 9 | MAINTENANCE | ON_LEAVE | DELIVERY | Fail - Multiple issues |
| 10 | OFFLINE | ACTIVE | DELIVERY | Fail - Drone offline |
| 11 | OFFLINE | INACTIVE | INSPECTION | Fail - Multiple issues |
| 12 | OFFLINE | ON_LEAVE | SURVEILLANCE | Fail - Multiple issues |

---

## 4. Test Case 3: Drone Status Transitions

### 4.1 Parameters and Values

| Parameter | Values | Count |
|-----------|--------|-------|
| Current Status | AVAILABLE, IN_FLIGHT, MAINTENANCE, CHARGING, OFFLINE | 5 |
| New Status | AVAILABLE, IN_FLIGHT, MAINTENANCE, CHARGING, OFFLINE | 5 |
| Battery Level | >80%, 50-80%, 20-50%, <20% | 4 |

**Total Possible Combinations**: 5 × 5 × 4 = 100

### 4.2 3-way Coverage Sample

Selected critical combinations for 3-way coverage:

| Test | Current Status | New Status | Battery | Valid? | Reason |
|------|----------------|------------|---------|--------|---------|
| 1 | AVAILABLE | IN_FLIGHT | >80% | Yes | Normal operation |
| 2 | AVAILABLE | IN_FLIGHT | <20% | No | Battery too low |
| 3 | IN_FLIGHT | AVAILABLE | >80% | Yes | Landing |
| 4 | IN_FLIGHT | CHARGING | 50-80% | No | Can't charge in flight |
| 5 | MAINTENANCE | AVAILABLE | >80% | Yes | Maintenance complete |
| 6 | MAINTENANCE | IN_FLIGHT | >80% | No | Must go to AVAILABLE first |
| 7 | CHARGING | AVAILABLE | >80% | Yes | Charge complete |
| 8 | OFFLINE | MAINTENANCE | Any | Yes | Going for repair |

---

## 5. Test Execution

### 5.1 Tools
- **JUnit 5**: Unit test framework
- **Mockito**: Mocking framework
- **ACTS (NIST)**: Combinatorial test generation tool (optional)
- **Maven Surefire**: Test runner

### 5.2 Automation
All combinatorial test cases should be automated using JUnit parameterized tests:

```java
@ParameterizedTest
@CsvSource({
    "Valid, Valid, Valid, AVAILABLE, SUCCESS",
    "Valid, Empty, Empty, MAINTENANCE, VALIDATION_ERROR",
    // ... more test cases
})
void testDroneCreationCombinations(
    String serialType, String modelType, 
    String manufacturerType, DroneStatus status, 
    String expectedResult) {
    // Test implementation
}
```

### 5.3 Coverage Goals
- **2-way coverage**: 100% for all parameter pairs
- **3-way coverage**: 80% for critical workflows
- **All valid combinations**: Must pass
- **All invalid combinations**: Must fail gracefully

---

## 6. Defect Classification

### 6.1 Priority Levels
1. **P1 - Critical**: System allows invalid operations (security/data integrity)
2. **P2 - High**: Incorrect error messages or unexpected failures
3. **P3 - Medium**: Poor user experience but functionally correct
4. **P4 - Low**: Minor cosmetic issues

### 6.2 Expected vs. Actual Results

| Combination Type | Expected Behavior | If Fails |
|------------------|-------------------|----------|
| All Valid | Success (200/201) | P1 |
| 1 Invalid | Appropriate error (400/409) | P2 |
| Multiple Invalid | First error reported | P3 |
| Edge Cases | Documented behavior | P2-P3 |

---

## 7. Test Metrics

### 7.1 Coverage Metrics
- **Parameter Coverage**: 100% (all values tested)
- **2-way Coverage**: 100% (all pairs tested)
- **3-way Coverage**: ≥80% (critical triplets)
- **Branch Coverage**: ≥85% (code paths)

### 7.2 Success Criteria
✓ All test cases executed
✓ All P1 defects resolved
✓ ≥95% P2 defects resolved
✓ Coverage goals met
✓ Zero regressions

---

## 8. Appendix A: Test Data Examples

### Valid Test Data
```json
{
  "serialNumber": "DRN-001",
  "model": "Phantom 4 Pro",
  "manufacturer": "DJI"
}
```

### Invalid Test Data
```json
{
  "serialNumber": "",
  "model": "@#$%",
  "manufacturer": "123456"
}
```

### Edge Cases
```json
{
  "serialNumber": "DRN-999999999999",
  "model": "A",
  "manufacturer": "日本メーカー"  // Unicode
}
```

---

## 9. Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | May 2026 | Ryan | Initial version |

---

## 10. Approval

**Prepared by**: Ryan  
**Course**: SEIS 739  
**Instructor**: [Instructor Name]  
**Date**: May 2026
