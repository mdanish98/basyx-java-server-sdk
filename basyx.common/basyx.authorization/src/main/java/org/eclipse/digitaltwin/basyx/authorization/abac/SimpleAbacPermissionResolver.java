/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.authorization.SubjectInformation;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.authorization.abac.AttributeItem.Global;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * An abstract permission resolver for {@link TargetInformation}
 * 
 * @param <T>
 * 
 */
public class SimpleAbacPermissionResolver implements AbacPermissionResolver {

	private Logger logger = LoggerFactory.getLogger(SimpleAbacPermissionResolver.class);
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	private AbacStorage abacStorage;
	private RoleProvider roleAuthenticator;
	private SubjectInformationProvider<Object> subjectInformationProvider;

	public SimpleAbacPermissionResolver(AbacStorage abacStorage, RoleProvider roleAuthenticator, SubjectInformationProvider<Object> subjectInformationProvider) {
		super();
		this.abacStorage = abacStorage;
		this.roleAuthenticator = roleAuthenticator;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	public AbacStorage getAbacStorage() {
		return abacStorage;
	}

	public RoleProvider getRoleProvider() {
		return roleAuthenticator;
	}

	// Function that checks if a query satisfies any rule
	public boolean hasPermission(RightsEnum rightsEnum, ObjectItem objectItem, Map<String, Value> attributesMap) {
		// LogicalComponent queryParameter = querySchema.getQueryParameter(); //
		// LogicalComponent type
		// if (queryParameter == null) {
		// throw new IllegalArgumentException("Invalid query: missing
		// 'queryParameter'");
		// }
		//
		List<AccessPermissionRule> allRules = abacStorage.getAbacRules();

		List<AccessPermissionRule> filteredRules = filterAccessRules(allRules, rightsEnum, objectItem);
		SubjectInformation<Object> subjectInfo = getSubjectInformation();
		Jwt jwt = (Jwt) subjectInfo.get();

		boolean accessGranted = validateAccessRules(filteredRules, attributesMap, jwt);

		return accessGranted;
	}
	
	private static Object getNestedClaim(Jwt jwt, String claim) {
        String[] keys = claim.split("\\.");
        Object value = jwt.getClaims();

        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(key);
            } else {
                return null; // Key path does not exist or invalid structure
            }
        }

        // Check the final value type
        if (value instanceof String) {
            return value;
        } else if (value instanceof List) {
            return value;
        } else {
            return null; // Unsupported type
        }
    }

	private static List<AccessPermissionRule> filterAccessRules(List<AccessPermissionRule> accessRules, RightsEnum rightsEnum, ObjectItem objectItem) {
		return accessRules.stream().filter(rule -> containsRightForAction(rule.getAcl().getRights(), rightsEnum)).filter(rule -> Acl.Access.ALLOW.equals(rule.getAcl().getAccess()))
				.filter(rule -> objectMatches(rule.getObjects(), objectItem)).toList();
	}

	private static boolean containsRightForAction(List<RightsEnum> list, RightsEnum rightsEnum) {
		return list.contains(rightsEnum);
	}

	private static boolean objectMatches(List<ObjectItem> objects, ObjectItem objectItem) {

		if (objects.size() > 1)
			return false;

		if (objects.get(0).getRoute() != null)
			return objects.get(0).getRoute().equals(objectItem.getRoute());

		if (objects.get(0).getIdentifiable() != null)
			return objects.get(0).getIdentifiable().equals("(AAS)*") || objects.get(0).getIdentifiable().equals(objectItem.getIdentifiable());

		return false;
	}

	private static boolean validateAccessRules(List<AccessPermissionRule> filteredRules, Map<String, Value> attributesMap, Jwt jwt) {
		
		return filteredRules.stream().anyMatch(rule -> evaluateFormula(rule.getFormula(), attributesMap, populateAttrItemsMapWithAttributeValue(rule.getAcl().getAttributes(), jwt)));
	}

	private static Map<String, Object> populateAttrItemsMapWithAttributeValue(List<AttributeItem> attributeItems, Jwt jwt) {
		
		Map<String, Object> attributeItemsMap = new HashMap<String, Object>();
		
		List<String> claims = attributeItems.stream().map(attributeItem -> attributeItem.getClaim()).filter(Objects::nonNull).collect(Collectors.toList());
		List<Global> globals = attributeItems.stream().map(attributeItem -> attributeItem.getGlobal()).filter(Objects::nonNull).collect(Collectors.toList());
			
		for (String claim : claims) {
			Object nestedClaimObject = getNestedClaim(jwt, claim);
			
			if (nestedClaimObject != null)
				attributeItemsMap.put("CLAIM#" + claim, nestedClaimObject);
		}
		
		for (Global global : globals) {
			if (global == Global.UTCNOW) {
//				ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
				
				LocalTime utcNow = ZonedDateTime.now(ZoneId.of("UTC")).toLocalTime();
				
//				Value value =  new Value();
//				value.setTimeVal(utcNow.toString());
				
				attributeItemsMap.put("GLOBAL#UTCNOW", utcNow);
			} else if (global == Global.LOCALNOW) {
//				LocalDateTime localNow = LocalDateTime.now();
				
				LocalTime localNow = LocalTime.now();
				
				attributeItemsMap.put("GLOBAL#LOCALNOW", localNow);
			}
		}
		
		return attributeItemsMap;
	}

	private static boolean evaluateFormula(LogicalExpression formula, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {

		if (formula.get$and() != null && !formula.get$and().isEmpty()) {
			// All child expressions in $and must match
			return formula.get$and().stream().allMatch(rule -> evaluateFormula(rule, attributesMap, attributeItemsMap));
		}

		if (formula.get$or() != null && !formula.get$or().isEmpty()) {
			// At least one child expression in $or must match
			return formula.get$or().stream().anyMatch(rule -> evaluateFormula(rule, attributesMap, attributeItemsMap));
		}

		if (formula.get$not() != null) {
			// Negation: query must not match the $not condition
			return !evaluateFormula(formula.get$not(), attributesMap, attributeItemsMap);
		}

		if (formula.get$eq() != null && !formula.get$eq().isEmpty()) {
			return evaluateEquality(formula.get$eq(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$ne() != null && !formula.get$ne().isEmpty()) {
			return evaluateInequality(formula.get$ne(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$le() != null && !formula.get$le().isEmpty()) {
			return evaluateLessOrEqual(formula.get$le(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$lt() != null && !formula.get$lt().isEmpty()) {
			return evaluateLessThan(formula.get$lt(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$ge() != null && !formula.get$ge().isEmpty()) {
			return evaluateGreaterOrEqual(formula.get$ge(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$gt() != null && !formula.get$gt().isEmpty()) {
			return evaluateGreaterThan(formula.get$gt(), attributesMap, attributeItemsMap);
		}
		// Add more cases for other operators ($gt, $lt, etc.)
		return false;
	}

	private static boolean evaluateEquality(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;

	    if (operands.get(0).get$field() != null && operands.get(0).get$field().startsWith("$aas")) {
	        return evaluateFieldlEquality(operands, attributesMap);
	    }

	    if (operands.get(0).get$attribute() != null) {
	        return evaluateAttributeEquality(operands, attributeItemsMap);
	    }

	    return false;
	}

	private static boolean evaluateFieldlEquality(List<Value> operands, Map<String, Value> attributesMap) {
	    String field = operands.get(0).get$field();

	    if (operands.get(1).get$strVal() != null) {
	        String ruleValue = operands.get(1).get$strVal();

	        if (attributesMap.containsKey(field)) {
	            String objectValue = attributesMap.get(field).get$strVal();
	            return ruleValue.equals(objectValue);
	        }
	    } else if (operands.get(1).get$timeVal() != null) {
	        String ruleValue = operands.get(1).get$timeVal();

	        if (attributesMap.containsKey(field)) {
	            String objectValue = attributesMap.get(field).get$timeVal();
	            return ruleValue.equals(objectValue);
	        }
	    }
	    return false;
	}

	private static boolean evaluateAttributeEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    AttributeItem ruleAttributeItem = operands.get(0).get$attribute();

	    if (ruleAttributeItem.getClaim() != null) {
	        return evaluateClaimEquality(operands, attributeItemsMap);
	    } else if (ruleAttributeItem.getGlobal() != null) {
	        return evaluateGlobalEquality(operands, attributeItemsMap);
	    }
	    return false;
	}

	private static boolean evaluateClaimEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    String ruleClaimItem = operands.get(0).get$attribute().getClaim();
	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
	        Object objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem);
	        String ruleAttributeClaimValue = operands.get(1).get$strVal();

	        if (ruleAttributeClaimValue != null) {
	        	
	        	if (objectAttributeClaimValue instanceof String)
	        		return ruleAttributeClaimValue.equals((String) objectAttributeClaimValue);
	        	else if (objectAttributeClaimValue instanceof List) {
	        		List<String> claimValueList = (List<String>) objectAttributeClaimValue;
	        		
	        		return claimValueList.contains(ruleAttributeClaimValue);
				}
	        }
	    }
	    return false;
	}

	private static boolean evaluateGlobalEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    Global ruleGlobalItem = operands.get(0).get$attribute().getGlobal();

	    if (ruleGlobalItem == Global.LOCALNOW) {
	        System.out.println("The Global enum is set to LOCALNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#LOCALNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#LOCALNOW");

	            if (operands.get(1).get$attribute() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$timeVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return !ruleTime.isBefore(objectAttributeGlobalValue) && !ruleTime.isAfter(objectAttributeGlobalValue);
	            } else {
	                return false;
	            }
	        } else {
	            return false;
	        }
	    } else if (ruleGlobalItem == Global.UTCNOW) {
	        System.out.println("The Global enum is set to UTCNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#UTCNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#UTCNOW");

	            if (operands.get(1).get$strVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$timeVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return !ruleTime.isBefore(objectAttributeGlobalValue) && !ruleTime.isAfter(objectAttributeGlobalValue);
	            } else {
	                return false;
	            }
	        } else {
	            return false;
	        }
	    } else if (ruleGlobalItem == Global.CLIENTNOW) {
	        System.out.println("The Global enum is set to CLIENTNOW - Not Supported");
	        return false;
	    } else if (ruleGlobalItem == Global.ANONYMOUS) {
	        System.out.println("The Global enum is set to ANONYMOUS - Not Supported");
	        return false;
	    } else {
	        System.out.println("Unknown Global enum value");
	        return false;
	    }
	}


	private static boolean evaluateInequality(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    // Implement logic for $ne
	    return !evaluateEquality(operands, attributesMap, attributeItemsMap);
	}

	private static boolean evaluateGreaterThan(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) > 0;
	}

	private static boolean evaluateGreaterOrEqual(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) >= 0;
	}

	private static boolean evaluateLessThan(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) < 0;
	}

	private static boolean evaluateLessOrEqual(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) <= 0;
	}

	private static boolean evaluateContains(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).get$strVal() != null &&
	           operands.get(0).get$strVal() != null &&
	           operands.get(0).get$strVal().contains(operands.get(1).get$strVal());
	}

	private static boolean evaluateStartsWith(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).get$strVal() != null &&
	           operands.get(0).get$strVal() != null &&
	           operands.get(0).get$strVal().startsWith(operands.get(1).get$strVal());
	}

	private static boolean evaluateEndsWith(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).get$strVal() != null &&
	           operands.get(0).get$strVal() != null &&
	           operands.get(0).get$strVal().endsWith(operands.get(1).get$strVal());
	}

	private static int compareValues(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    Value operand1Value = operands.get(0);
	    
	    if (operand1Value.get$field() != null && operand1Value.get$field().startsWith("$aas")) {
	        return evaluateFieldComparison(operands, attributesMap);
	    }
	    
	    if (operand1Value.get$attribute() != null) {
	        return evaluateAttributeComparison(operands, attributeItemsMap);
	    }

	    return 0;
	}
	
	private static int evaluateFieldComparison(List<Value> operands, Map<String, Value> attributesMap) {
	    String field = operands.get(0).get$field();

	    if (operands.get(1).get$strVal() != null) {
	        String ruleValue = operands.get(1).get$strVal();

	        if (attributesMap.containsKey(field)) {
	            String objectValue = attributesMap.get(field).get$strVal();
	            return ruleValue.compareTo(objectValue);
	        }
	    } else if (operands.get(1).get$timeVal() != null) {
	        String ruleTimeValueString = operands.get(1).get$timeVal();
	        
	        LocalTime ruleTimeValue = LocalTime.parse(ruleTimeValueString);

	        if (attributesMap.containsKey(field)) {
	            String objectTimeValueString = attributesMap.get(field).get$timeVal();
	            
	            LocalTime objectTimeValue = LocalTime.parse(objectTimeValueString);
	            
	            return ruleTimeValue.compareTo(objectTimeValue);
	        }
	    }
	    return 0;
	}
	
	private static int evaluateAttributeComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    AttributeItem ruleAttributeItem = operands.get(0).get$attribute();

	    if (ruleAttributeItem.getClaim() != null) {
	        return evaluateClaimComparison(operands, attributeItemsMap);
	    } else if (ruleAttributeItem.getGlobal() != null) {
	        return evaluateGlobalComparison(operands, attributeItemsMap);
	    }
	    return 0;
	}
	
	private static int evaluateClaimComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    String ruleClaimItem = operands.get(0).get$attribute().getClaim();
	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
	        Object objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem);
	        String ruleAttributeClaimValue = operands.get(1).get$strVal();

	        if (ruleAttributeClaimValue != null) {
	        	
	        	if (objectAttributeClaimValue instanceof String)
	        		return ruleAttributeClaimValue.compareTo((String) objectAttributeClaimValue);
	        	else if (objectAttributeClaimValue instanceof List) {
	        		List<String> claimValueList = (List<String>) objectAttributeClaimValue;
	        		
	        		return 0;
				}
	        }
	    }
	    return 0;
	}
	
	private static int evaluateGlobalComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    Global ruleGlobalItem = operands.get(0).get$attribute().getGlobal();

	    if (ruleGlobalItem == Global.LOCALNOW) {
	        System.out.println("The Global enum is set to LOCALNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#LOCALNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#LOCALNOW");

	            if (operands.get(1).get$strVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return ruleTime.compareTo(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	            	String ruleTimeValueString = operands.get(1).get$timeVal();
		            
	            	LocalTime ruleTime = LocalTime.parse(ruleTimeValueString, FORMATTER);
		            
		            return objectAttributeGlobalValue.compareTo(ruleTime);
	            } else {
	                return 0;
	            }
	        } else {
	            return 0;
	        }
	    } else if (ruleGlobalItem == Global.UTCNOW) {
	        System.out.println("The Global enum is set to UTCNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#UTCNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#UTCNOW");

	            if (operands.get(1).get$strVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return ruleTime.compareTo(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	            	String ruleTimeValueString = operands.get(1).get$timeVal();
		            
	            	LocalTime ruleTime = LocalTime.parse(ruleTimeValueString, FORMATTER);
		            
		            return objectAttributeGlobalValue.compareTo(ruleTime);
	            } else {
	                return 0;
	            }
	        } else {
	            return 0;
	        }
	    } else if (ruleGlobalItem == Global.CLIENTNOW) {
	        System.out.println("The Global enum is set to CLIENTNOW - Not Supported");
	        return 0;
	    } else if (ruleGlobalItem == Global.ANONYMOUS) {
	        System.out.println("The Global enum is set to ANONYMOUS - Not Supported");
	        return 0;
	    } else {
	        System.out.println("Unknown Global enum value");
	        return 0;
	    }
	}


	// ###############################################################################################################################################################

	// Recursive method to evaluate logical and simple expressions
	private boolean isRuleMatchingQuery(LogicalExpression query, LogicalExpression accessRule) {
		if (accessRule.get$and() != null && !accessRule.get$and().isEmpty()) {
			// All child expressions in $and must match
			return accessRule.get$and().stream().allMatch(rule -> query.get$and().stream().allMatch(query1 -> isRuleMatchingQuery(query1, rule)));
		}

		if (accessRule.get$or() != null && !accessRule.get$or().isEmpty()) {
			// At least one child expression in $or must match
			return accessRule.get$or().stream().anyMatch(rule -> query.get$or().stream().allMatch(query1 -> isRuleMatchingQuery(query1, rule)));
		}

		if (accessRule.get$not() != null) {
			// Negation: query must not match the $not condition
			return !isRuleMatchingQuery(query.get$not(), accessRule.get$not());
		}

		if (accessRule.get$eq() != null && !accessRule.get$eq().isEmpty()) {
			// Check equality
			return query.get$eq() != null && query.get$eq().equals(accessRule.get$eq());
		}

		if (accessRule.get$ne() != null && !accessRule.get$ne().isEmpty()) {
			// Check inequality
			return query.get$ne() != null && !query.get$ne().equals(accessRule.get$ne());
		}

		// Repeat similar logic for $gt, $ge, $lt, $le, $contains, etc.
		// Example for $contains:
		if (accessRule.get$contains() != null && !accessRule.get$contains().isEmpty()) {
			return query.get$contains() != null && query.get$contains().containsAll(accessRule.get$contains());
		}

		// Add other conditions as needed...

		// Default case: return false if no conditions are matched
		return false;
	}

	public boolean matchesAny(LogicalExpression query, List<LogicalExpression> accessRules) {
		for (LogicalExpression accessRule : accessRules) {
			if (isRuleMatchingQuery(query, accessRule)) {
				return true;
			}
		}
		return false;
	}

	//
	// // Evaluate simple expressions (e.g., $eq, $gt)
	// private boolean evaluateSimpleExpression(AllRule rule, SimpleExpression
	// simpleExpression) {
	// if (simpleExpression.get$eq() != null) {
	// return evaluateEquality(rule, simpleExpression.get$eq());
	// }
	// if (simpleExpression.get$gt() != null) {
	// return evaluateGreaterThan(rule, simpleExpression.get$gt());
	// }
	// // Add other evaluations ($lt, $contains, etc.)
	// return false;
	// }
	//
	// // Evaluate $eq condition by checking if the rule's formula satisfies the
	// query formula
	// private boolean evaluateEquality(AllRule rule, Object eqCondition) {
	// if (!(eqCondition instanceof List) || ((List<?>) eqCondition).size() != 2) {
	// throw new IllegalArgumentException("Invalid $eq condition: must have exactly
	// two elements");
	// }
	//
	// List<?> conditions = (List<?>) eqCondition;
	// String left = conditions.get(0).toString();
	// String right = conditions.get(1).toString();
	//
	// LogicalComponent ruleFormula = rule.getFormula();
	// if (ruleFormula == null) {
	// return false; // Base case: If the rule has no formula, return false
	// }
	//
	// // Create a SimpleExpression to represent the query condition
	// SimpleExpression queryExpression = new SimpleExpression();
	// queryExpression.set$eq(List.of(left, right));
	//
	// // Check if the rule's formula matches the query condition
	// return matchFormula(ruleFormula, queryExpression);
	// }
	//
	//
	// private boolean matchFormula(LogicalComponent ruleFormula, LogicalComponent
	// queryFormula) {
	// // Base case: If both are simple expressions, compare directly
	// if (ruleFormula instanceof SimpleExpression && queryFormula instanceof
	// SimpleExpression) {
	// return compareSimpleExpressions((SimpleExpression) ruleFormula,
	// (SimpleExpression) queryFormula);
	// }
	//
	// // Recursive case: If the rule is a logical expression, evaluate its
	// conditions
	// if (ruleFormula instanceof LogicalExpression__1) {
	// LogicalExpression__1 logicalRule = (LogicalExpression__1) ruleFormula;
	//
	// if (logicalRule.get$and() != null && !logicalRule.get$and().isEmpty()) {
	// // Check if the query formula satisfies all AND conditions
	// for (LogicalComponent condition : logicalRule.get$and()) {
	// if (!matchFormula(condition, queryFormula)) {
	// return false; // One AND condition fails
	// }
	// }
	// return true; // All AND conditions pass
	// }
	//
	// if (logicalRule.get$or() != null && !logicalRule.get$or().isEmpty()) {
	// // Check if the query formula satisfies at least one OR condition
	// for (LogicalComponent condition : logicalRule.get$or()) {
	// if (matchFormula(condition, queryFormula)) {
	// return true; // At least one OR condition matches
	// }
	// }
	// return false; // No OR condition matches
	// }
	//
	// if (logicalRule.get$not() != null) {
	// // Negate the match result for NOT condition
	// return !matchFormula(logicalRule.get$not(), queryFormula);
	// }
	// }
	//
	// // Default: If no match criteria met, return false
	// return false;
	// }
	//
	// private boolean compareSimpleExpressions(SimpleExpression ruleExpr,
	// SimpleExpression queryExpr) {
	// // Compare $eq conditions
	// if (ruleExpr.get$eq() != null && queryExpr.get$eq() != null) {
	// List<?> ruleEq = (List<?>) ruleExpr.get$eq();
	// List<?> queryEq = (List<?>) queryExpr.get$eq();
	// return ruleEq.equals(queryEq); // Compare the $eq lists
	// }
	//
	// // Add comparisons for other types like $gt, $lt if needed
	// return false;
	// }
	//
	//
	//
	// // Evaluate $gt condition
	// private boolean evaluateGreaterThan(AllRule rule, Object gtCondition) {
	// if (!(gtCondition instanceof List) || ((List<?>) gtCondition).size() != 2) {
	// throw new IllegalArgumentException("Invalid $gt condition: must have exactly
	// two elements");
	// }
	//
	// List<?> conditions = (List<?>) gtCondition;
	// double left = getNumericValue(rule, conditions.get(0));
	// double right = getNumericValue(rule, conditions.get(1));
	//
	// return left > right;
	// }
	//
	// // Helper function to extract numeric values
	// private double getNumericValue(AllRule rule, Object node) {
	// if (node instanceof Number) {
	// return ((Number) node).doubleValue();
	// } else if (node instanceof String) {
	// String attributeName = (String) node;
	// String value = getAttributeValueFromRule(rule, attributeName);
	// if (value != null) {
	// return Double.parseDouble(value);
	// }
	// }
	// throw new IllegalArgumentException("Invalid numeric value in condition");
	// }
	//
	// // Retrieve attribute value from rule
	// private String getAttributeValueFromRule(AllRule rule, String attributeName)
	// {
	// for (AttributeItem attribute : rule.getAttributes()) {
	// if (attribute.getClaim() != null &&
	// attribute.getClaim().equals(attributeName)) {
	// return attribute.getClaim();
	// }
	// }
	// return null;
	// }
	//
	// // Check if rule attributes match JWT claims
	// private boolean areAttributesMatching(AllRule rule, Jwt jwt) {
	// for (AttributeItem attribute : rule.getAttributes()) {
	// if (attribute.getClaim() != null) {
	// String claimValue = jwt.getClaim(attribute.getClaim());
	// if (claimValue == null || !claimValue.equals(attribute.getClaim())) {
	// return false;
	// }
	// }
	// }
	// return true;
	// }
	//
	// // Check if required rights match
	// private boolean areRightsMatching(List<RightsEnum> requiredRights,
	// List<RightsEnum> queryRights) {
	// return requiredRights.containsAll(queryRights);
	// }
	//
	// // Check if access is allowed
	// private boolean isAccessAllowed(AllRule rule) {
	// return rule.getAccess() == AllRule.Access.ALLOW;
	// }
	//
	// // Evaluate object matching (stub for now)
	// private boolean areObjectsMatching(AllRule rule) {
	// return true; // Assume all objects match for now
	// }
	//
	// // Get subject information
	private SubjectInformation<Object> getSubjectInformation() {
		SubjectInformation<Object> subjectInfo = subjectInformationProvider.get();

		if (subjectInfo == null)
			throw new NullSubjectException("Subject information is null.");

		return subjectInfo;
	}
}
