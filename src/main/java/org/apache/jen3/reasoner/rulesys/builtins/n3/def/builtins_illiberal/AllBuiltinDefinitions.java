package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import java.util.List;
import java.util.ArrayList;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class AllBuiltinDefinitions {

    public static List<BuiltinDefinition> getAll() {
        List<BuiltinDefinition> allBuiltins = new ArrayList<>();
        allBuiltins.add(new StringNotGreaterThan());
        allBuiltins.add(new MathAbsoluteValue());
        allBuiltins.add(new MathAcos());
        allBuiltins.add(new LogSemanticsOrError());
        allBuiltins.add(new MathQuotient());
        allBuiltins.add(new StringContainsIgnoringCase());
        allBuiltins.add(new StringLowerCase());
        allBuiltins.add(new MathEqualTo());
        allBuiltins.add(new MathFloor());
        allBuiltins.add(new GraphDifference());
        allBuiltins.add(new LogLanglit());
        allBuiltins.add(new LogSemantics());
        allBuiltins.add(new ListNotIn());
        allBuiltins.add(new LogNameSpace());
        allBuiltins.add(new TimeMonth());
        allBuiltins.add(new CryptoSha());
        allBuiltins.add(new ListAppend());
        allBuiltins.add(new LogNotIncludes());
        allBuiltins.add(new MathMax());
        allBuiltins.add(new StringEqualIgnoringCase());
        allBuiltins.add(new ListSort());
        allBuiltins.add(new MathAsinh());
        allBuiltins.add(new MathNotGreaterThan());
        allBuiltins.add(new MathMin());
        allBuiltins.add(new MathRadians());
        allBuiltins.add(new MathAcosh());
        allBuiltins.add(new StringJoin());
        allBuiltins.add(new LogLocalName());
        allBuiltins.add(new MathRemainder());
        allBuiltins.add(new MathLessThan());
        allBuiltins.add(new LogCollectAllIn());
        allBuiltins.add(new MathCeiling());
        allBuiltins.add(new LogUri());
        allBuiltins.add(new StringNotEqualIgnoringCase());
        allBuiltins.add(new ListNotMember());
        allBuiltins.add(new LogLabel());
        allBuiltins.add(new LogPrefix());
        allBuiltins.add(new TimeDay());
        allBuiltins.add(new LogForAllIn());
        allBuiltins.add(new MathExponentiation());
        allBuiltins.add(new StringContainsRoughly());
        allBuiltins.add(new ListMember());
        allBuiltins.add(new StringReplaceAll());
        allBuiltins.add(new StringEndsWith());
        allBuiltins.add(new ListLength());
        allBuiltins.add(new LogConclusion());
        allBuiltins.add(new TimeMinute());
        allBuiltins.add(new MathGreaterThan());
        allBuiltins.add(new StringScrape());
        allBuiltins.add(new GraphLength());
        allBuiltins.add(new GraphMember());
        allBuiltins.add(new TimeHour());
        allBuiltins.add(new FileListFiles());
        allBuiltins.add(new ListUnique());
        allBuiltins.add(new ListIn());
        allBuiltins.add(new LogConjunction());
        allBuiltins.add(new ListLast());
        allBuiltins.add(new MathRoundedTo());
        allBuiltins.add(new MathNotLessThan());
        allBuiltins.add(new StringFormat());
        allBuiltins.add(new LogSkolem());
        allBuiltins.add(new ListRemove());
        allBuiltins.add(new MathLogarithm());
        allBuiltins.add(new ListRemoveAt());
        allBuiltins.add(new MathNotEqualTo());
        allBuiltins.add(new MathDegrees());
        allBuiltins.add(new TimeTimeZone());
        allBuiltins.add(new StringNotContainsRoughly());
        allBuiltins.add(new LogOutputString());
        allBuiltins.add(new MathSinh());
        allBuiltins.add(new ListIterate());
        allBuiltins.add(new MathRounded());
        allBuiltins.add(new LogBound());
        allBuiltins.add(new TimeInSeconds());
        allBuiltins.add(new StringContains());
        allBuiltins.add(new MathCos());
        allBuiltins.add(new MathSum());
        allBuiltins.add(new StringConcatenation());
        allBuiltins.add(new MathTan());
        allBuiltins.add(new MathDifference());
        allBuiltins.add(new LogEqualTo());
        allBuiltins.add(new StringNotMatches());
        allBuiltins.add(new MathProduct());
        allBuiltins.add(new ListMemberAt());
        allBuiltins.add(new StringLength());
        allBuiltins.add(new StringReplace());
        allBuiltins.add(new StringGreaterThan());
        allBuiltins.add(new LogNotEqualTo());
        allBuiltins.add(new ListFirst());
        allBuiltins.add(new StringMatches());
        allBuiltins.add(new StringNotLessThan());
        allBuiltins.add(new LogUuid());
        allBuiltins.add(new StringLessThan());
        allBuiltins.add(new LogDtlit());
        allBuiltins.add(new StringUpperCase());
        allBuiltins.add(new LogContent());
        allBuiltins.add(new TimeCurrentTime());
        allBuiltins.add(new MathAsin());
        allBuiltins.add(new LogInferences());
        allBuiltins.add(new MathAtanh());
        allBuiltins.add(new MathNegation());
        allBuiltins.add(new LogIncludes());
        allBuiltins.add(new MathAtan());
        allBuiltins.add(new LogRawType());
        allBuiltins.add(new MathTanh());
        allBuiltins.add(new StringSubstring());
        allBuiltins.add(new StringStartsWith());
        allBuiltins.add(new LogParsedAsN3());
        allBuiltins.add(new ListRemoveDuplicates());
        allBuiltins.add(new MathCosh());
        allBuiltins.add(new StringCapitalize());
        allBuiltins.add(new StringScrapeAll());
        allBuiltins.add(new TimeDayOfWeek());
        allBuiltins.add(new TimeSecond());
        allBuiltins.add(new TimeYear());
        allBuiltins.add(new MathSin());
        return allBuiltins;
    }
}
