import analyzer.InputUtils.parseArgs
import analyzer.LogAnalyzer.analyzeLogRecords
import analyzer.LogParser.{getLogRecords, openFileSource, openURLSource}
import analyzer.LogReport
import analyzer.LogReport.createReport
import cats.effect.{ExitCode, IO, IOApp}
import analyzer.FileUtils.getMatchedFile

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      exitCode <- parseArgs(args) match
        case Some(parsedArgs) =>
          if (parsedArgs.path.startsWith("http")) {
            openURLSource(parsedArgs.path).bracket { source =>
              for {
                logRecords <- getLogRecords(
                  source,
                  parsedArgs.from,
                  parsedArgs.to,
                  parsedArgs.filterField,
                  parsedArgs.filterValue
                )
                logReport <- IO.pure(
                  analyzeLogRecords(
                    logRecords,
                    LogReport(
                      parsedArgs.path,
                      parsedArgs.from,
                      parsedArgs.to,
                      None,
                      List[Long]()
                    )
                  )
                )
                _ <- IO(createReport(logReport, parsedArgs.format))
              } yield ExitCode.Success
            } { source =>
              println("close stream")
              IO(source.close())
            }
          } else {
            val initialReport = LogReport(
              parsedArgs.path,
              parsedArgs.from,
              parsedArgs.to,
              None,
              List[Long]()
            )
            val files = getMatchedFile(parsedArgs.path)

            files
              .foldLeft(IO.pure(initialReport)) { (currentReport, file) =>
                currentReport.flatMap { currentReport =>
                  openFileSource(file).bracket { source =>
                    for {
                      logRecords <- getLogRecords(
                        source,
                        parsedArgs.from,
                        parsedArgs.to,
                        parsedArgs.filterField,
                        parsedArgs.filterValue
                      )
                      updatedReport <- IO.pure(
                        analyzeLogRecords(logRecords, currentReport)
                      )
                    } yield updatedReport
                  } { source =>
                    IO(source.close())
                  }
                }
              }
              .flatMap(finalReport =>
                IO(createReport(finalReport, parsedArgs.format))
                  .map(_ => ExitCode.Success)
              )
          }
        case None => IO.pure(ExitCode.Error)
    } yield exitCode
  }
}
