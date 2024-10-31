import analyzer.InputUtils.parseArgs
import analyzer.LogAnalyzer.analyzeLogRecords
import analyzer.LogParser.getLogRecords
import analyzer.LogReport.createReport

@main
def main(args: String*): Unit = {
  parseArgs(args) match
    case Some(parsedArgs) =>
      val logRecords = getLogRecords(
        parsedArgs.path,
        parsedArgs.from,
        parsedArgs.to,
        parsedArgs.filterField,
        parsedArgs.filterValue
      )
      val logReport = analyzeLogRecords(
        logRecords,
        parsedArgs.path,
        parsedArgs.from,
        parsedArgs.to
      )
      createReport(logReport, parsedArgs.format)
    case None => println("Wrong args")
}
