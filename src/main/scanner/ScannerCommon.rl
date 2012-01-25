%%{

    machine ScannerCommon;
    
    nl = '\n' | '\r\n' | '\r' | '\f';
    nonascii = 128..255;
    escape = '\\' ( xdigit{1,6} space? | ( ' ' | '-' | '~' | nonascii ) );
    nmstart = [_a-zA-Z] | nonascii | escape;
    nmchar = [_a-zA-Z0-9\-] | nonascii | escape;
    string1 = '"' ( [\t !#$%&\(-~] | '\\' nl | "'" | nonascii | escape )* '"';
    string2 = "'" ( [\t !#$%&\(-~] | '\\' nl | '"' | nonascii | escape )* "'";

    ident = '-'? nmstart nmchar*;
    num = digit+ | digit* '.' digit+;
    string = string1 | string2;
    s = space+;

    combinator = ( '+' | '>' | '~' ) >combinator space* | space+;
    hash = '#' nmchar+ >mark %id;
    clazz = '.' ident >mark %clazz;
    match = [~|^$*]? '=';
    attrib = '[' s* ident >mark %attr_name ( s* match >mark %attr_match s* ( ident | string ) >mark %attr_value )? s* ']' >attr;
    nth = ( [+\-]? digit* 'n' ( s* [+\-] s* digit+ )? ) | [+\-]? digit+ | 'odd' | 'even';
    pseudo_nth = ':' ( ident - 'not' ) >mark %pseudo_nth_class '(' s* nth >mark %pseudo_nth_arg <: s* ')';
    pseudo_class = ':' ident >mark %pseudo_class;
    pseudo_element = '::' ident;
    pseudo = pseudo_element | pseudo_class | pseudo_nth;
    negation = ':not' '(' >_negation @{ fcall neg; };
    specifier = ( hash | clazz | attrib | pseudo | negation );

    element_name = ident >mark %tag;
    universal = '*' >mark %tag;
    type_selector = element_name;
    simple_selector = ( ( type_selector | universal ) specifier* | specifier+ ) %sel s*;
    selector = simple_selector ( combinator simple_selector )*;
    selectors = ( selector >_group %group ) <: ( ',' s* selector >_group %group )**;
    
    negation_specifier = ( hash | clazz | attrib | pseudo_class | pseudo_nth );
    # We are a bit relaxed on the spec here. According to the spec it should only be one of
    # type_selector, universal, hash, clazz, attrib or pseudo (except elements and not).
    negation_simple_selector = ( ( type_selector | universal ) negation_specifier* | negation_specifier+ ) %sel s*;
    neg := s* negation_simple_selector s* ')' >negation @{ fret; };

}%%;
