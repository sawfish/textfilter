package fiu.kdrg.textfilter.custom;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class WholeFileInputFormat extends FileInputFormat<Text, Text> {

  @Override
  protected boolean isSplitable(JobContext context, Path filename) {
    // TODO Auto-generated method stub
    return false;
  }
  

  @Override
  public RecordReader<Text, Text> createRecordReader(InputSplit split,
      TaskAttemptContext context) throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    WholeFileRecordReader reader = new WholeFileRecordReader();
    reader.initialize(split, context);
    return reader;
  }

}
