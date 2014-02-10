package cs.fiu.edu.textfilter.filter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cs.fiu.edu.textfilter.custom.WholeFileInputFormat;

public class XmlPageFilterDriver extends Configured implements Tool {

  public static class FilterMapper extends
      Mapper<NullWritable, Text, Text, Text> {

  }

  public static class FilterReducer extends
      Reducer<Text, Text, NullWritable, NullWritable> {

  }

  public int run(String[] args) throws Exception {
    // TODO Auto-generated method stub

    Configuration conf = getConf();
    Job filterJob = new Job(conf, "Wikipedia page filter job");
    filterJob.setJarByClass(XmlPageFilterDriver.class);
    filterJob.setMapperClass(FilterMapper.class);
    filterJob.setReducerClass(FilterReducer.class);

    FileSystem fs = FileSystem.get(conf);
    FileStatus[] list = fs.listStatus(new Path(args[0]));
    if (list != null) {
      for (FileStatus status : list) {
        FileInputFormat.addInputPath(filterJob, status.getPath());
      }
    }

    // WholeFileInputFormat
    filterJob.setInputFormatClass(WholeFileInputFormat.class);
    filterJob.setMapOutputKeyClass(Text.class);
    filterJob.setMapOutputValueClass(Text.class);

    filterJob.setOutputFormatClass(NullOutputFormat.class);
    filterJob.setNumReduceTasks(1);

    filterJob.waitForCompletion(true);
    return 0;
  }

  public static void main(String[] args) throws Exception{
    int exit = ToolRunner.run(new XmlPageFilterDriver(), args);
    System.exit(exit);
  }
}
