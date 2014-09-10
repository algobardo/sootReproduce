import java.io.{FileOutputStream, FileInputStream, File, FilenameFilter}
import java.util
import java.util.Iterator
import java.util.Map
import java.util.zip.{ZipFile, GZIPInputStream}
import soot._
import soot.jimple.AbstractStmtSwitch
import soot.jimple.InvokeExpr
import soot.jimple.InvokeStmt
import soot.jimple.Jimple
import soot.jimple.StringConstant
import soot.options.Options;
import org.rogach.scallop._
import soot.util.PhaseDumper
;
import scala.collection.JavaConversions._

//class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
//  val outDir = opt[String]()
//  val frameworkInstrumentation = toggle("frameworkInstrumentation")
//}


object entry {


  def sootFrameworkInstrumentClasses(pathin: File, pathout: File, additionalClasspath: String): scala.Unit = {
    println("analysing..." + pathin + " output in " + pathout)
    G.reset()
//    Options.v().set_verbose(true);

    Options.v().set_src_prec(Options.src_prec_class)
    Options.v().set_output_format(Options.output_format_class)

    Options.v().set_allow_phantom_refs(true)
        Options.v().set_keep_line_number(true)
    //    Options.v().set_whole_program(true)

    val procDir = new util.ArrayList[String]()
    procDir.add(pathin.getAbsolutePath())

    Options.v().set_process_dir(procDir)
    Options.v().set_output_dir(pathout.getAbsolutePath)

    Options.v().set_soot_classpath(additionalClasspath)

    Options.v().set_include_all(true)
    val excluded = new util.LinkedList[String]()

    excluded.add("android.util") /* Failing jb on MathUtils */

    Options.v().no_bodies_for_excluded()
    Options.v().set_exclude(excluded)



    /* Debugging */
//    val dumpBodies = new util.LinkedList[String]()
//    dumpBodies.add("jb.ls")
//    dumpBodies.add("jtp")
//    dumpBodies.add("bb")
//    dumpBodies.add("bb.lso")
//    dumpBodies.add("bb.pho")
//    dumpBodies.add("bb.ule")
//    dumpBodies.add("bb.lp")
//    Options.v().set_dump_body(dumpBodies)

    /* Workaround for the bug of bb.lp */
    // PhaseOptions.v().setPhaseOption("bb.lp","off")


    Scene.v().loadNecessaryClasses()
    Scene.v().loadBasicClasses()


    val forcedLibrary = new util.LinkedList[String]()
//    forcedLibrary.add("android.graphics.Color")
    /*
    EXCEPTION FROM SIMULATION:
    return type mismatch: prototype indicates int, but encountered type long

    ...at bytecode offset 00000023
    locals[0000]: Ljava/lang/String;
    locals[0001]: J
    locals[0002]: <invalid>
      locals[0003]: <invalid>
        ...while working on block 001e
        ...while working on method parseColor:(Ljava/lang/String;)I
        ...while processing parseColor (Ljava/lang/String;)I
        ...while processing android/graphics/Color.class
    */

//    forcedLibrary.add("android.widget.TextClock")
    /*
      UNEXPECTED TOP-LEVEL EXCEPTION:
    com.android.dx.cf.iface.ParseException: severely truncated class file
      at com.android.dx.cf.direct.DirectClassFile.parse0(DirectClassFile.java:454)
      at com.android.dx.cf.direct.DirectClassFile.parse(DirectClassFile.java:406)
      at com.android.dx.cf.direct.DirectClassFile.parseToInterfacesIfNecessary(DirectClassFile.java:388)
      at com.android.dx.cf.direct.DirectClassFile.getMagic(DirectClassFile.java:251)
      at com.android.dx.command.dexer.Main.processClass(Main.java:665)
      at com.android.dx.command.dexer.Main.processFileBytes(Main.java:634)
      at com.android.dx.command.dexer.Main.access$600(Main.java:78)
      at com.android.dx.command.dexer.Main$1.processFileBytes(Main.java:572)
      at com.android.dx.cf.direct.ClassPathOpener.processArchive(ClassPathOpener.java:284)
      at com.android.dx.cf.direct.ClassPathOpener.processOne(ClassPathOpener.java:166)
      at com.android.dx.cf.direct.ClassPathOpener.process(ClassPathOpener.java:144)
      at com.android.dx.command.dexer.Main.processOne(Main.java:596)
      at com.android.dx.command.dexer.Main.processAllFiles(Main.java:498)
      at com.android.dx.command.dexer.Main.runMonoDex(Main.java:264)
      at com.android.dx.command.dexer.Main.run(Main.java:230)
      at com.android.dx.command.dexer.Main.main(Main.java:199)
      at com.android.dx.command.Main.main(Main.java:103)
    ...while parsing android/widget/TextClock.class
     */


// Disables instrumentation of specific class
    // val allClasses = Scene.v().getClasses.iterator()
    // while(allClasses.hasNext){

    //   val cl = allClasses.next
    //   if(cl.resolvingLevel() == SootClass.BODIES &&
    //     (cl.implementsInterface("java.lang.annotation.Annotation")
    //       || forcedLibrary.contains(cl.getName))
    //   ) {
    //     println("Excluded " + cl)
    //     cl.setLibraryClass()
    //   }

    // }

    PackManager.v().runPacks()

    if(!Options.v().oaat())
      PackManager.v().writeOutput()


  }



  def main(args: Array[String]): scala.Unit = {
    
    if(args.size == 0) {
      sootFrameworkInstrumentClasses(new File("./res/classes"), new File("./out/"), "./classpath/core.jar:./classpath/ext.jar:./classpath/bouncycastle.jar:./classpath/conscrypt.jar:./classpath/corejunit.jar")
    }
    
  }
}