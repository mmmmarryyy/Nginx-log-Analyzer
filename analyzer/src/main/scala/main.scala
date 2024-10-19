import analyzer.InputUtils.parseArgs
import analyzer.LogAnalyzer.analyzeLogRecords
import analyzer.LogParser.getLogRecords
import analyzer.LogReport.createReport

@main
def main(args: String*): Unit = {
  parseArgs(args) match
    case Some(parsedArgs) =>
      val (path, from, to, format) = parsedArgs

      val logRecords = getLogRecords(path, from, to)
      val logReport = analyzeLogRecords(logRecords, path, from, to)
      createReport(logReport, format)
    case None => println("Wrong args")
}
