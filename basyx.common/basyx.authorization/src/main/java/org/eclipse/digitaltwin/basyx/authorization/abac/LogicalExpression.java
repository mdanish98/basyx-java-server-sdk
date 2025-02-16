
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$and",
    "$match",
    "$or",
    "$not",
    "$eq",
    "$ne",
    "$gt",
    "$ge",
    "$lt",
    "$le",
    "$contains",
    "$starts-with",
    "$ends-with",
    "$regex",
    "$boolean"
})
@Generated("jsonschema2pojo")
public class LogicalExpression {

    @JsonProperty("$and")
    private List<LogicalExpression> $and = new ArrayList<LogicalExpression>();
    @JsonProperty("$match")
    private List<MatchExpression> $match = new ArrayList<MatchExpression>();
    @JsonProperty("$or")
    private List<LogicalExpression> $or = new ArrayList<LogicalExpression>();
    @JsonProperty("$not")
    private LogicalExpression $not;
    @JsonProperty("$eq")
    private List<Value> $eq = new ArrayList<Value>();
    @JsonProperty("$ne")
    private List<Value> $ne = new ArrayList<Value>();
    @JsonProperty("$gt")
    private List<Value> $gt = new ArrayList<Value>();
    @JsonProperty("$ge")
    private List<Value> $ge = new ArrayList<Value>();
    @JsonProperty("$lt")
    private List<Value> $lt = new ArrayList<Value>();
    @JsonProperty("$le")
    private List<Value> $le = new ArrayList<Value>();
    @JsonProperty("$contains")
    private List<StringValue> $contains = new ArrayList<StringValue>();
    @JsonProperty("$starts-with")
    private List<StringValue> $startsWith = new ArrayList<StringValue>();
    @JsonProperty("$ends-with")
    private List<StringValue> $endsWith = new ArrayList<StringValue>();
    @JsonProperty("$regex")
    private List<StringValue> $regex = new ArrayList<StringValue>();
    @JsonProperty("$boolean")
    private Boolean $boolean;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LogicalExpression() {
    }

    /**
     * 
     * @param $boolean
     * @param $and
     * @param $ge
     * @param $endsWith
     * @param $or
     * @param $regex
     * @param $startsWith
     * @param $lt
     * @param $contains
     * @param $eq
     * @param $gt
     * @param $ne
     * @param $match
     * @param $not
     * @param $le
     */
    public LogicalExpression(List<LogicalExpression> $and, List<MatchExpression> $match, List<LogicalExpression> $or, LogicalExpression $not, List<Value> $eq, List<Value> $ne, List<Value> $gt, List<Value> $ge, List<Value> $lt, List<Value> $le, List<StringValue> $contains, List<StringValue> $startsWith, List<StringValue> $endsWith, List<StringValue> $regex, Boolean $boolean) {
        super();
        this.$and = $and;
        this.$match = $match;
        this.$or = $or;
        this.$not = $not;
        this.$eq = $eq;
        this.$ne = $ne;
        this.$gt = $gt;
        this.$ge = $ge;
        this.$lt = $lt;
        this.$le = $le;
        this.$contains = $contains;
        this.$startsWith = $startsWith;
        this.$endsWith = $endsWith;
        this.$regex = $regex;
        this.$boolean = $boolean;
    }

    @JsonProperty("$and")
    public List<LogicalExpression> get$and() {
        return $and;
    }

    @JsonProperty("$and")
    public void set$and(List<LogicalExpression> $and) {
        this.$and = $and;
    }

    @JsonProperty("$match")
    public List<MatchExpression> get$match() {
        return $match;
    }

    @JsonProperty("$match")
    public void set$match(List<MatchExpression> $match) {
        this.$match = $match;
    }

    @JsonProperty("$or")
    public List<LogicalExpression> get$or() {
        return $or;
    }

    @JsonProperty("$or")
    public void set$or(List<LogicalExpression> $or) {
        this.$or = $or;
    }

    @JsonProperty("$not")
    public LogicalExpression get$not() {
        return $not;
    }

    @JsonProperty("$not")
    public void set$not(LogicalExpression $not) {
        this.$not = $not;
    }

    @JsonProperty("$eq")
    public List<Value> get$eq() {
        return $eq;
    }

    @JsonProperty("$eq")
    public void set$eq(List<Value> $eq) {
        this.$eq = $eq;
    }

    @JsonProperty("$ne")
    public List<Value> get$ne() {
        return $ne;
    }

    @JsonProperty("$ne")
    public void set$ne(List<Value> $ne) {
        this.$ne = $ne;
    }

    @JsonProperty("$gt")
    public List<Value> get$gt() {
        return $gt;
    }

    @JsonProperty("$gt")
    public void set$gt(List<Value> $gt) {
        this.$gt = $gt;
    }

    @JsonProperty("$ge")
    public List<Value> get$ge() {
        return $ge;
    }

    @JsonProperty("$ge")
    public void set$ge(List<Value> $ge) {
        this.$ge = $ge;
    }

    @JsonProperty("$lt")
    public List<Value> get$lt() {
        return $lt;
    }

    @JsonProperty("$lt")
    public void set$lt(List<Value> $lt) {
        this.$lt = $lt;
    }

    @JsonProperty("$le")
    public List<Value> get$le() {
        return $le;
    }

    @JsonProperty("$le")
    public void set$le(List<Value> $le) {
        this.$le = $le;
    }

    @JsonProperty("$contains")
    public List<StringValue> get$contains() {
        return $contains;
    }

    @JsonProperty("$contains")
    public void set$contains(List<StringValue> $contains) {
        this.$contains = $contains;
    }

    @JsonProperty("$starts-with")
    public List<StringValue> get$startsWith() {
        return $startsWith;
    }

    @JsonProperty("$starts-with")
    public void set$startsWith(List<StringValue> $startsWith) {
        this.$startsWith = $startsWith;
    }

    @JsonProperty("$ends-with")
    public List<StringValue> get$endsWith() {
        return $endsWith;
    }

    @JsonProperty("$ends-with")
    public void set$endsWith(List<StringValue> $endsWith) {
        this.$endsWith = $endsWith;
    }

    @JsonProperty("$regex")
    public List<StringValue> get$regex() {
        return $regex;
    }

    @JsonProperty("$regex")
    public void set$regex(List<StringValue> $regex) {
        this.$regex = $regex;
    }

    @JsonProperty("$boolean")
    public Boolean get$boolean() {
        return $boolean;
    }

    @JsonProperty("$boolean")
    public void set$boolean(Boolean $boolean) {
        this.$boolean = $boolean;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogicalExpression.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("$and");
        sb.append('=');
        sb.append(((this.$and == null)?"<null>":this.$and));
        sb.append(',');
        sb.append("$match");
        sb.append('=');
        sb.append(((this.$match == null)?"<null>":this.$match));
        sb.append(',');
        sb.append("$or");
        sb.append('=');
        sb.append(((this.$or == null)?"<null>":this.$or));
        sb.append(',');
        sb.append("$not");
        sb.append('=');
        sb.append(((this.$not == null)?"<null>":this.$not));
        sb.append(',');
        sb.append("$eq");
        sb.append('=');
        sb.append(((this.$eq == null)?"<null>":this.$eq));
        sb.append(',');
        sb.append("$ne");
        sb.append('=');
        sb.append(((this.$ne == null)?"<null>":this.$ne));
        sb.append(',');
        sb.append("$gt");
        sb.append('=');
        sb.append(((this.$gt == null)?"<null>":this.$gt));
        sb.append(',');
        sb.append("$ge");
        sb.append('=');
        sb.append(((this.$ge == null)?"<null>":this.$ge));
        sb.append(',');
        sb.append("$lt");
        sb.append('=');
        sb.append(((this.$lt == null)?"<null>":this.$lt));
        sb.append(',');
        sb.append("$le");
        sb.append('=');
        sb.append(((this.$le == null)?"<null>":this.$le));
        sb.append(',');
        sb.append("$contains");
        sb.append('=');
        sb.append(((this.$contains == null)?"<null>":this.$contains));
        sb.append(',');
        sb.append("$startsWith");
        sb.append('=');
        sb.append(((this.$startsWith == null)?"<null>":this.$startsWith));
        sb.append(',');
        sb.append("$endsWith");
        sb.append('=');
        sb.append(((this.$endsWith == null)?"<null>":this.$endsWith));
        sb.append(',');
        sb.append("$regex");
        sb.append('=');
        sb.append(((this.$regex == null)?"<null>":this.$regex));
        sb.append(',');
        sb.append("$boolean");
        sb.append('=');
        sb.append(((this.$boolean == null)?"<null>":this.$boolean));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.$boolean == null)? 0 :this.$boolean.hashCode()));
        result = ((result* 31)+((this.$and == null)? 0 :this.$and.hashCode()));
        result = ((result* 31)+((this.$ge == null)? 0 :this.$ge.hashCode()));
        result = ((result* 31)+((this.$endsWith == null)? 0 :this.$endsWith.hashCode()));
        result = ((result* 31)+((this.$or == null)? 0 :this.$or.hashCode()));
        result = ((result* 31)+((this.$regex == null)? 0 :this.$regex.hashCode()));
        result = ((result* 31)+((this.$startsWith == null)? 0 :this.$startsWith.hashCode()));
        result = ((result* 31)+((this.$lt == null)? 0 :this.$lt.hashCode()));
        result = ((result* 31)+((this.$contains == null)? 0 :this.$contains.hashCode()));
        result = ((result* 31)+((this.$eq == null)? 0 :this.$eq.hashCode()));
        result = ((result* 31)+((this.$gt == null)? 0 :this.$gt.hashCode()));
        result = ((result* 31)+((this.$ne == null)? 0 :this.$ne.hashCode()));
        result = ((result* 31)+((this.$match == null)? 0 :this.$match.hashCode()));
        result = ((result* 31)+((this.$not == null)? 0 :this.$not.hashCode()));
        result = ((result* 31)+((this.$le == null)? 0 :this.$le.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LogicalExpression) == false) {
            return false;
        }
        LogicalExpression rhs = ((LogicalExpression) other);
        return ((((((((((((((((this.$boolean == rhs.$boolean)||((this.$boolean!= null)&&this.$boolean.equals(rhs.$boolean)))&&((this.$and == rhs.$and)||((this.$and!= null)&&this.$and.equals(rhs.$and))))&&((this.$ge == rhs.$ge)||((this.$ge!= null)&&this.$ge.equals(rhs.$ge))))&&((this.$endsWith == rhs.$endsWith)||((this.$endsWith!= null)&&this.$endsWith.equals(rhs.$endsWith))))&&((this.$or == rhs.$or)||((this.$or!= null)&&this.$or.equals(rhs.$or))))&&((this.$regex == rhs.$regex)||((this.$regex!= null)&&this.$regex.equals(rhs.$regex))))&&((this.$startsWith == rhs.$startsWith)||((this.$startsWith!= null)&&this.$startsWith.equals(rhs.$startsWith))))&&((this.$lt == rhs.$lt)||((this.$lt!= null)&&this.$lt.equals(rhs.$lt))))&&((this.$contains == rhs.$contains)||((this.$contains!= null)&&this.$contains.equals(rhs.$contains))))&&((this.$eq == rhs.$eq)||((this.$eq!= null)&&this.$eq.equals(rhs.$eq))))&&((this.$gt == rhs.$gt)||((this.$gt!= null)&&this.$gt.equals(rhs.$gt))))&&((this.$ne == rhs.$ne)||((this.$ne!= null)&&this.$ne.equals(rhs.$ne))))&&((this.$match == rhs.$match)||((this.$match!= null)&&this.$match.equals(rhs.$match))))&&((this.$not == rhs.$not)||((this.$not!= null)&&this.$not.equals(rhs.$not))))&&((this.$le == rhs.$le)||((this.$le!= null)&&this.$le.equals(rhs.$le))));
    }

}
