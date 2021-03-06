/* Generated By:JavaCC: Do not edit this line. RPCCompiler.java */
package org.commoncrawl.rpc.compiler.generated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.commoncrawl.rpc.compiler.JBoolean;
import org.commoncrawl.rpc.compiler.JBuffer;
import org.commoncrawl.rpc.compiler.JByte;
import org.commoncrawl.rpc.compiler.JComparator;
import org.commoncrawl.rpc.compiler.JDouble;
import org.commoncrawl.rpc.compiler.JEnum;
import org.commoncrawl.rpc.compiler.JEnumValue;
import org.commoncrawl.rpc.compiler.JField;
import org.commoncrawl.rpc.compiler.JFile;
import org.commoncrawl.rpc.compiler.JFloat;
import org.commoncrawl.rpc.compiler.JInt;
import org.commoncrawl.rpc.compiler.JLong;
import org.commoncrawl.rpc.compiler.JMap;
import org.commoncrawl.rpc.compiler.JMethod;
import org.commoncrawl.rpc.compiler.JModule;
import org.commoncrawl.rpc.compiler.JRecord;
import org.commoncrawl.rpc.compiler.JService;
import org.commoncrawl.rpc.compiler.JString;
import org.commoncrawl.rpc.compiler.JType;
import org.commoncrawl.rpc.compiler.JVector;

public class RPCCompiler implements RPCCompilerConstants {
  private static String                      language     = "java";
  private static String                      destDir      = ".";
  private static ArrayList<String>           recFiles     = new ArrayList<String>();
  private static ArrayList<String>           cmdargs      = new ArrayList<String>();
  private static JFile                       curFile;
  private static Hashtable<String, JRecord>  recTab       = new Hashtable<String, JRecord>();
  private static Hashtable<String, JService> serviceTable = new Hashtable<String, JService>();
  private static String                      curMethod    = "";
  private static String                      curService   = "";
  private static String                      curParamType = "";
  private static String                      curDir       = ".";
  private static String                      curFileName;
  private static String                      curModuleName;

  public static void main(String[] args) {
    System.exit(driver(args));
  }

  public static void usage() {
    System.err.println("Usage: RPCCompiler --language [java] ddl-files");
  }

  public static int driver(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if ("-l".equalsIgnoreCase(args[i])
          || "--language".equalsIgnoreCase(args[i])) {
        language = args[i + 1].toLowerCase();
        i++;
      } else if ("-d".equalsIgnoreCase(args[i])
          || "--destdir".equalsIgnoreCase(args[i])) {
        destDir = args[i + 1];
        i++;
      } else if (args[i].startsWith("-")) {
        String arg = args[i].substring(1);
        if (arg.startsWith("-")) {
          arg = arg.substring(1);
        }
        cmdargs.add(arg.toLowerCase());
      } else {
        recFiles.add(args[i]);
      }
    }
    if (recFiles.size() == 0) {
      usage();
      return 1;
    }
    for (int i = 0; i < recFiles.size(); i++) {
      curFileName = recFiles.get(i);
      File file = new File(curFileName);
      curDir = file.getParent();

      System.out.println("\n****PARSING FILE:" + file.toString() + "\n");
      try {
        FileReader reader = new FileReader(file);
        RPCCompiler parser = new RPCCompiler(reader);
        try {
          recTab = new Hashtable<String, JRecord>();
          curFile = parser.Input();
        } catch (ParseException e) {
          System.err.println(e.toString());
          return 1;
        }
        try {
          reader.close();
        } catch (IOException e) {
        }
      } catch (FileNotFoundException e) {
        System.err.println("File " + (String) recFiles.get(i) + " Not found.");
        return 1;
      }
      try {
        int retCode = curFile.genCode(language, destDir, cmdargs);
        if (retCode != 0) {
          return retCode;
        }
      } catch (IOException e) {
        System.err.println(e.toString());
        return 1;
      }
    }
    return 0;
  }

  final public JFile Input() throws ParseException {
    ArrayList<JFile> ilist = new ArrayList<JFile>();
    ArrayList<JRecord> rlist = new ArrayList<JRecord>();
    ArrayList<JService> serviceList = new ArrayList<JService>();
    JFile i;
    ArrayList<JRecord> l;
    JModule module;
    label_1: while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case INCLUDE_TKN:
          i = Include();
          ilist.add(i);
          break;
        case MODULE_TKN:
          module = Module();
          rlist.addAll(module.getRecords());
          serviceList.addAll(module.getServices());
          break;
        default:
          jj_la1[0] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      }
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case MODULE_TKN:
        case INCLUDE_TKN:
          ;
          break;
        default:
          jj_la1[1] = jj_gen;
          break label_1;
      }
    }
    jj_consume_token(0);
    {
      if (true)
        return new JFile(curFileName, ilist, rlist, serviceList);
    }
    throw new Error("Missing return statement in function");
  }

  final public JFile Include() throws ParseException {
    String fname;
    Token t;
    jj_consume_token(INCLUDE_TKN);
    t = jj_consume_token(CSTRING_TKN);
    JFile ret = null;
    fname = t.image.replaceAll("^\"", "").replaceAll("\"$", "");
    File file = new File(curDir, fname);
    String tmpDir = curDir;
    String tmpFile = curFileName;
    curDir = file.getParent();
    curFileName = file.getName();
    try {
      FileReader reader = new FileReader(file);
      RPCCompiler parser = new RPCCompiler(reader);
      try {
        ret = parser.Input();
        System.out.println(fname + " Parsed Successfully");
      } catch (ParseException e) {
        System.out.println(e.toString());
        System.exit(1);
      }
      try {
        reader.close();
      } catch (IOException e) {
      }
    } catch (FileNotFoundException e) {
      System.out.println("tmpDir is:" + tmpDir);
      System.out.println("File " + fname + " Not found. Path:"
          + file.getAbsolutePath());
      System.exit(1);
    }
    curDir = tmpDir;
    curFileName = tmpFile;
    {
      if (true)
        return ret;
    }
    throw new Error("Missing return statement in function");
  }

  final public JModule Module() throws ParseException {
    JModule module;
    String mName;
    JRecord record;
    JService service;
    jj_consume_token(MODULE_TKN);
    mName = ModuleName();
    System.out.println("Module Name:" + mName);
    curModuleName = mName;
    module = new JModule(mName);
    jj_consume_token(LBRACE_TKN);
    label_2: while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case SERVICE_TKN:
        case RECORD_TKN:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_2;
      }
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case SERVICE_TKN:
          service = Service();
          module.addService(service);
          break;
        case RECORD_TKN:
          record = Record();
          if (record != null)
            System.out.println("Record() returned:" + record.toString());
          else
            System.out.println("Record() returned NULL");

          module.addRecord(record);
          break;
        default:
          jj_la1[3] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      }
    }
    jj_consume_token(RBRACE_TKN);
    {
      if (true)
        return module;
    }
    throw new Error("Missing return statement in function");
  }

  final public String ModuleName() throws ParseException {
    String name = "";
    Token t;
    t = jj_consume_token(IDENT_TKN);
    name += t.image;
    label_3: while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case DOT_TKN:
          ;
          break;
        default:
          jj_la1[4] = jj_gen;
          break label_3;
      }
      jj_consume_token(DOT_TKN);
      t = jj_consume_token(IDENT_TKN);
      name += "." + t.image;
    }
    {
      if (true)
        return name;
    }
    throw new Error("Missing return statement in function");
  }

  final public JService Service() throws ParseException {
    String name;
    ArrayList<JMethod> methodList = new ArrayList<JMethod>();
    Token t;
    JMethod m;
    jj_consume_token(SERVICE_TKN);
    t = jj_consume_token(IDENT_TKN);
    name = t.image;
    curService = name;
    jj_consume_token(LBRACE_TKN);
    label_4: while (true) {
      m = Method();
      methodList.add(m);
      jj_consume_token(SEMICOLON_TKN);
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case METHOD_TKN:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_4;
      }
    }
    jj_consume_token(RBRACE_TKN);
    String fqn = curModuleName + "." + name;
    JService service = new JService(fqn, methodList);
    serviceTable.put(fqn, service);
    {
      if (true)
        return service;
    }
    throw new Error("Missing return statement in function");
  }

  final public JMethod Method() throws ParseException {
    String inputType = null;
    String outputType = null;
    Token name;
    JRecord inputAnonymousRecord = null;
    JRecord outputAnonymousRecord = null;
    jj_consume_token(METHOD_TKN);
    name = jj_consume_token(IDENT_TKN);
    curMethod = name.toString();
    jj_consume_token(LPAREN_TKN);
    jj_consume_token(IN_TKN);
    curParamType = "input";
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case IDENT_TKN:
        inputType = ModuleName();
        break;
      case LBRACE_TKN:
        inputAnonymousRecord = AnonymousRecord();
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }
    jj_consume_token(COMMA_TKN);
    jj_consume_token(OUT_TKN);
    curParamType = "output";
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case IDENT_TKN:
        outputType = ModuleName();
        break;
      case LBRACE_TKN:
        outputAnonymousRecord = AnonymousRecord();
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }
    jj_consume_token(RPAREN_TKN);
    JRecord inputRecordType = null;
    JRecord outputRecordType = null;
    if (inputAnonymousRecord != null) {
      inputRecordType = inputAnonymousRecord;
    } else if (inputType.compareTo("NullMessage") != 0) {

      if (inputType.indexOf('.', 0) < 0) {
        inputType = curModuleName + "." + inputType;
      }

      inputRecordType = recTab.get(inputType);

      if (inputRecordType == null) {
        System.out.println("Input Param of Type " + inputType
            + " not known. Exiting.");
        System.exit(1);
      }
    }
    if (outputAnonymousRecord != null) {
      outputRecordType = outputAnonymousRecord;
    } else if (outputType.compareTo("NullMessage") != 0) {

      if (outputType.indexOf('.', 0) < 0) {
        outputType = curModuleName + "." + outputType;
      }

      outputRecordType = recTab.get(outputType);

      if (outputRecordType == null) {
        System.out.println("Output Param of Type " + outputType
            + " not known. Exiting.");
        System.exit(1);
      }
    }
    {
      if (true)
        return new JMethod(name.image, inputRecordType, outputRecordType);
    }
    throw new Error("Missing return statement in function");
  }

  final public Set<String> RecordModifiers() throws ParseException {
    String modifier;
    Set<String> modifiers = new HashSet<String>();
    jj_consume_token(47);
    modifier = RecordModifier();
    modifiers.add(modifier);
    label_5: while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case COMMA_TKN:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_5;
      }
      jj_consume_token(COMMA_TKN);
      modifier = RecordModifier();
      modifiers.add(modifier);
    }
    jj_consume_token(48);
    {
      if (true)
        return modifiers;
    }
    throw new Error("Missing return statement in function");
  }

  final public String RecordModifier() throws ParseException {
    Token modifier;
    modifier = jj_consume_token(IDENT_TKN);
    {
      if (true)
        return modifier.toString();
    }
    throw new Error("Missing return statement in function");
  }

  final public JRecord Record() throws ParseException {
    System.out.println("Entering Record Constructor");
    String rname;
    ArrayList<JField<JType>> flist = new ArrayList<JField<JType>>();
    ArrayList<JEnum> enumList = new ArrayList<JEnum>();
    ArrayList<JComparator> comparators = new ArrayList<JComparator>();
    Token t;
    JField<JType> f;
    JEnum e;
    JComparator c;
    Set<String> modifiers = null;
    jj_consume_token(RECORD_TKN);
    t = jj_consume_token(IDENT_TKN);
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case 47:
        modifiers = RecordModifiers();
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
    }
    rname = t.image;
    jj_consume_token(LBRACE_TKN);
    label_6: while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case ENUM_TKN:
          e = Enumeration();
          enumList.add(e);
          break;
        case BYTE_TKN:
        case BOOLEAN_TKN:
        case INT_TKN:
        case LONG_TKN:
        case VINT_TKN:
        case VLONG_TKN:
        case FLOAT_TKN:
        case DOUBLE_TKN:
        case USTRING_TKN:
        case BUFFER_TKN:
        case VECTOR_TKN:
        case MAP_TKN:
        case IDENT_TKN:
        case 47:
          f = Field();
          jj_consume_token(SEMICOLON_TKN);
          flist.add(f);
          break;
        case COMPARATOR_TKN:
          c = Comparator();
          comparators.add(c);
          break;
        default:
          jj_la1[10] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      }
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case ENUM_TKN:
        case COMPARATOR_TKN:
        case BYTE_TKN:
        case BOOLEAN_TKN:
        case INT_TKN:
        case LONG_TKN:
        case VINT_TKN:
        case VLONG_TKN:
        case FLOAT_TKN:
        case DOUBLE_TKN:
        case USTRING_TKN:
        case BUFFER_TKN:
        case VECTOR_TKN:
        case MAP_TKN:
        case IDENT_TKN:
        case 47:
          ;
          break;
        default:
          jj_la1[11] = jj_gen;
          break label_6;
      }
    }
    jj_consume_token(RBRACE_TKN);
    String fqn = curModuleName + "." + rname;
    System.out.println("fqn:" + fqn);
    JRecord r = new JRecord(fqn, flist, enumList, modifiers, comparators);
    recTab.put(fqn, r);
    {
      if (true)
        return r;
    }
    throw new Error("Missing return statement in function");
  }

  final public JRecord AnonymousRecord() throws ParseException {
    System.out.println("Entering AnonymousRecord Constructor");
    ArrayList<JField<JType>> flist = new ArrayList<JField<JType>>();
    Token t;
    JField<JType> f;
    Set<String> modifiers = null;
    jj_consume_token(LBRACE_TKN);
    label_7: while (true) {
      f = Field();
      jj_consume_token(SEMICOLON_TKN);
      flist.add(f);
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case BYTE_TKN:
        case BOOLEAN_TKN:
        case INT_TKN:
        case LONG_TKN:
        case VINT_TKN:
        case VLONG_TKN:
        case FLOAT_TKN:
        case DOUBLE_TKN:
        case USTRING_TKN:
        case BUFFER_TKN:
        case VECTOR_TKN:
        case MAP_TKN:
        case IDENT_TKN:
        case 47:
          ;
          break;
        default:
          jj_la1[12] = jj_gen;
          break label_7;
      }
    }
    jj_consume_token(RBRACE_TKN);
    String fqn = curMethod + "_" + curParamType;
    JRecord r = new JRecord(fqn, flist, null, null, null);
    {
      if (true)
        return r;
    }
    throw new Error("Missing return statement in function");
  }

  final public JComparator Comparator() throws ParseException {
    Token name;
    ArrayList<String> fieldNames = new ArrayList<String>();
    Token fieldName;
    jj_consume_token(COMPARATOR_TKN);
    name = jj_consume_token(IDENT_TKN);
    jj_consume_token(LBRACE_TKN);
    label_8: while (true) {
      fieldName = jj_consume_token(IDENT_TKN);
      jj_consume_token(SEMICOLON_TKN);
      fieldNames.add(fieldName.toString());
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case IDENT_TKN:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_8;
      }
    }
    jj_consume_token(RBRACE_TKN);
    {
      if (true)
        return new JComparator(name.toString(), fieldNames);
    }
    throw new Error("Missing return statement in function");
  }

  final public JEnum Enumeration() throws ParseException {
    Token name;
    ArrayList<JEnumValue> values = new ArrayList<JEnumValue>();
    JEnumValue v;
    jj_consume_token(ENUM_TKN);
    name = jj_consume_token(IDENT_TKN);
    jj_consume_token(LBRACE_TKN);
    label_9: while (true) {
      v = EnumerationValue();
      values.add(v);
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case IDENT_TKN:
          ;
          break;
        default:
          jj_la1[14] = jj_gen;
          break label_9;
      }
    }
    jj_consume_token(RBRACE_TKN);
    {
      if (true)
        return new JEnum(name.image, values);
    }
    throw new Error("Missing return statement in function");
  }

  final public JEnumValue EnumerationValue() throws ParseException {
    Token valueName;
    Token ordinal;
    valueName = jj_consume_token(IDENT_TKN);
    jj_consume_token(EQUALS_TKN);
    ordinal = jj_consume_token(INTEGER_TKN);
    jj_consume_token(SEMICOLON_TKN);
    {
      if (true)
        return new JEnumValue(valueName.image, Integer.parseInt(ordinal.image));
    }
    throw new Error("Missing return statement in function");
  }

  final public int FieldModifiers() throws ParseException {
    int modifiers = 0;
    int modifier = 0;
    jj_consume_token(47);
    modifier = FieldModifier();
    modifiers |= modifier;
    label_10: while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case COMMA_TKN:
          ;
          break;
        default:
          jj_la1[15] = jj_gen;
          break label_10;
      }
      jj_consume_token(COMMA_TKN);
      modifier = FieldModifier();
      modifiers |= modifier;
    }
    jj_consume_token(48);
    {
      if (true)
        return modifiers;
    }
    throw new Error("Missing return statement in function");
  }

  final public int FieldModifier() throws ParseException {
    int modifier = 0;
    label_11: while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_11;
      }
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case KEY_TKN:
          jj_consume_token(KEY_TKN);
          modifier = JField.Modifiers.KEY;
          break;
        case TRANSIENT_TKN:
          jj_consume_token(TRANSIENT_TKN);
          modifier = JField.Modifiers.TRANSIENT;
          break;
        default:
          jj_la1[16] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      }
    }
    {
      if (true)
        return modifier;
    }
    throw new Error("Missing return statement in function");
  }

  final public JField<JType> Field() throws ParseException {
    JType jt;
    Token name;
    Token ordinal;
    int modifiers = 0;
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case 47:
        modifiers = FieldModifiers();
        break;
      default:
        jj_la1[17] = jj_gen;
        ;
    }
    jt = Type();
    name = jj_consume_token(IDENT_TKN);
    jj_consume_token(EQUALS_TKN);
    ordinal = jj_consume_token(INTEGER_TKN);
    {
      if (true)
        return new JField<JType>(name.image, jt, Integer
            .parseInt(ordinal.image), modifiers);
    }
    throw new Error("Missing return statement in function");
  }

  final public JType Type() throws ParseException {
    JType jt;
    Token t;
    String rname;
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case MAP_TKN:
        jt = Map();
        {
          if (true)
            return jt;
        }
        break;
      case VECTOR_TKN:
        jt = Vector();
        {
          if (true)
            return jt;
        }
        break;
      case BYTE_TKN:
        jj_consume_token(BYTE_TKN);
        {
          if (true)
            return new JByte();
        }
        break;
      case BOOLEAN_TKN:
        jj_consume_token(BOOLEAN_TKN);
        {
          if (true)
            return new JBoolean();
        }
        break;
      case INT_TKN:
        jj_consume_token(INT_TKN);
        {
          if (true)
            return new JInt(false);
        }
        break;
      case LONG_TKN:
        jj_consume_token(LONG_TKN);
        {
          if (true)
            return new JLong(false);
        }
        break;
      case VINT_TKN:
        jj_consume_token(VINT_TKN);
        {
          if (true)
            return new JInt(true);
        }
        break;
      case VLONG_TKN:
        jj_consume_token(VLONG_TKN);
        {
          if (true)
            return new JLong(true);
        }
        break;
      case FLOAT_TKN:
        jj_consume_token(FLOAT_TKN);
        {
          if (true)
            return new JFloat();
        }
        break;
      case DOUBLE_TKN:
        jj_consume_token(DOUBLE_TKN);
        {
          if (true)
            return new JDouble();
        }
        break;
      case USTRING_TKN:
        jj_consume_token(USTRING_TKN);
        {
          if (true)
            return new JString();
        }
        break;
      case BUFFER_TKN:
        jj_consume_token(BUFFER_TKN);
        {
          if (true)
            return new JBuffer();
        }
        break;
      case IDENT_TKN:
        rname = ModuleName();
        if (rname.indexOf('.', 0) < 0) {
          rname = curModuleName + "." + rname;
        }
        JRecord r = recTab.get(rname);
        if (r == null) {
          System.out.println("Type " + rname + " not known. Exiting.");
          System.exit(1);
        }
        {
          if (true)
            return r;
        }
        break;
      default:
        jj_la1[18] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public JMap Map() throws ParseException {
    JType jt1;
    JType jt2;
    jj_consume_token(MAP_TKN);
    jj_consume_token(LT_TKN);
    jt1 = Type();
    jj_consume_token(COMMA_TKN);
    jt2 = Type();
    jj_consume_token(GT_TKN);
    {
      if (true)
        return new JMap(jt1, jt2);
    }
    throw new Error("Missing return statement in function");
  }

  final public JVector Vector() throws ParseException {
    JType jt;
    jj_consume_token(VECTOR_TKN);
    jj_consume_token(LT_TKN);
    jt = Type();
    jj_consume_token(GT_TKN);
    {
      if (true)
        return new JVector(jt);
    }
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla;
    jj_lastpos = jj_scanpos = token;
    try {
      return !jj_3_1();
    } catch (LookaheadSuccess ls) {
      return true;
    } finally {
      jj_save(0, xla);
    }
  }

  final private boolean jj_3R_13() {
    if (jj_scan_token(TRANSIENT_TKN))
      return true;
    return false;
  }

  final private boolean jj_3_1() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_12()) {
      jj_scanpos = xsp;
      if (jj_3R_13())
        return true;
    }
    return false;
  }

  final private boolean jj_3R_12() {
    if (jj_scan_token(KEY_TKN))
      return true;
    return false;
  }

  public RPCCompilerTokenManager token_source;
  SimpleCharStream               jj_input_stream;
  public Token                   token, jj_nt;
  private int                    jj_ntk;
  private Token                  jj_scanpos, jj_lastpos;
  private int                    jj_la;
  public boolean                 lookingAhead = false;
  private boolean                jj_semLA;
  private int                    jj_gen;
  final private int[]            jj_la1       = new int[19];
  static private int[]           jj_la1_0;
  static private int[]           jj_la1_1;
  static {
    jj_la1_0();
    jj_la1_1();
  }

  private static void jj_la1_0() {
    jj_la1_0 = new int[] { 0x80800, 0x80800, 0x3000, 0x3000, 0x0, 0x4000, 0x0,
        0x0, 0x0, 0x0, 0xfff18000, 0xfff18000, 0xfff00000, 0x0, 0x0, 0x0, 0x0,
        0x0, 0xfff00000, };
  }

  private static void jj_la1_1() {
    jj_la1_1 = new int[] { 0x0, 0x0, 0x0, 0x0, 0x800, 0x0, 0x2008, 0x2008,
        0x400, 0x8000, 0xa000, 0xa000, 0xa000, 0x2000, 0x2000, 0x400, 0x3,
        0x8000, 0x2000, };
  }

  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean         jj_rescan = false;
  private int             jj_gc     = 0;

  public RPCCompiler(java.io.InputStream stream) {
    this(stream, null);
  }

  public RPCCompiler(java.io.InputStream stream, String encoding) {
    try {
      jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
    } catch (java.io.UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    token_source = new RPCCompilerTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++)
      jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++)
      jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    ReInit(stream, null);
  }

  public void ReInit(java.io.InputStream stream, String encoding) {
    try {
      jj_input_stream.ReInit(stream, encoding, 1, 1);
    } catch (java.io.UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++)
      jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++)
      jj_2_rtns[i] = new JJCalls();
  }

  public RPCCompiler(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new RPCCompilerTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++)
      jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++)
      jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++)
      jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++)
      jj_2_rtns[i] = new JJCalls();
  }

  public RPCCompiler(RPCCompilerTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++)
      jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++)
      jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(RPCCompilerTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++)
      jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++)
      jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null)
      token = token.next;
    else
      token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen)
              c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error {
  }

  final private LookaheadSuccess jj_ls = new LookaheadSuccess();

  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0;
      Token tok = token;
      while (tok != null && tok != jj_scanpos) {
        i++;
        tok = tok.next;
      }
      if (tok != null)
        jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind)
      return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos)
      throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null)
      token = token.next;
    else
      token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null)
        t = t.next;
      else
        t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt = token.next) == null)
      return (jj_ntk = (token.next = token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[]            jj_expentry;
  private int              jj_kind       = -1;
  private int[]            jj_lasttokens = new int[100];
  private int              jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100)
      return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e
          .hasMoreElements();) {
        int[] oldentry = (int[]) (e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists)
            break;
        }
      }
      if (!exists)
        jj_expentries.addElement(jj_expentry);
      if (pos != 0)
        jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[49];
    for (int i = 0; i < 49; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 19; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1 << j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1 << j)) != 0) {
            la1tokens[32 + j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 49; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[]) jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
      try {
        JJCalls p = jj_2_rtns[i];
        do {
          if (p.gen > jj_gen) {
            jj_la = p.arg;
            jj_lastpos = jj_scanpos = p.first;
            switch (i) {
              case 0:
                jj_3_1();
                break;
            }
          }
          p = p.next;
        } while (p != null);
      } catch (LookaheadSuccess ls) {
      }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) {
        p = p.next = new JJCalls();
        break;
      }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la;
    p.first = token;
    p.arg = xla;
  }

  static final class JJCalls {
    int     gen;
    Token   first;
    int     arg;
    JJCalls next;
  }

}
