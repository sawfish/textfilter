package cs.fiu.edu.textfilter.custom;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WholeFileRecordReader extends RecordReader<Text, Text> {

  private FileSplit split;
  private Configuration conf;
  
  private final BytesWritable currValue = new BytesWritable();
  private String fileName;

  private boolean fileProcessed = false;
  private Text value = new Text();

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public Text getCurrentKey() throws IOException, InterruptedException {
    return (new Text(fileName));
  }

  @Override
  public Text getCurrentValue() throws IOException, InterruptedException {
    return value;
  }

  @Override
  public float getProgress() throws IOException, InterruptedException {
    return 0;
  }

  @Override
  public void initialize(InputSplit split, TaskAttemptContext context)
      throws IOException, InterruptedException {
    this.split = (FileSplit)split;
    this.fileName = ((FileSplit) split).getPath().getName();
    this.conf = context.getConfiguration();
  }

  @Override
  public boolean nextKeyValue() throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    if (fileProcessed) {
      return false;
    }
    
    int fileLength = (int)split.getLength();
    byte [] result = new byte[fileLength];
    
    FileSystem  fs = FileSystem.get(conf);
    FSDataInputStream in = null; 
    try {
            in = fs.open( split.getPath());
            IOUtils.readFully(in, result, 0, fileLength);
            currValue.set(result, 0, fileLength);
            
    } finally {
            IOUtils.closeStream(in);
    }
    
    value.set(currValue.getBytes());
    this.fileProcessed = true;
    return true;
  }

}
