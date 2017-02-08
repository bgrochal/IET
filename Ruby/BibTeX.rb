require 'getoptlong'

options = GetoptLong.new(
  ['--help', '-h', GetoptLong::NO_ARGUMENT],
  ['--ascending', '-a', GetoptLong::NO_ARGUMENT], 
  ['--descending', '-d', GetoptLong::NO_ARGUMENT]
)

 sort_kind = nil

options.each do |option, argument|
  case option
  when '--help'
    puts <<-EOF
--help, -h: shows help;
--ascending, -a: sorts data in ascending order by the name of publication category;
--descending, -d: sorts data in descending order by the name of publication category.
EOF
  when '--ascending'
    option = "asc"
    sort_kind = "asc"
  when '--descending'
    option = "desc"
    sort_kind = "desc"
  end
end

input = File.open("data.bib", "r")
data = input.read
data.gsub!("\\", "\\\\")

if(sort_kind != nil)
  sorted = Hash.new("")
  data.scan(/((\n.+)+\s*})/) { |matched| sorted[matched[0].scan(/@(\w+)/)] = sorted[matched[0].scan(/@(\w+)/)] + "\n" + matched[0] }
  sorted_array = sorted.to_a
  sorted_array.sort!
  data = ""

  if(sort_kind === "desc")
    sorted_array.reverse!
  end

  sorted_array.each { |value| data << value[1] }
end

output = File.new("bibliography.html", "w")
output.puts("<!DOCTYPE html><html><head><tile>Zadanie domowe nr 1</title><body>")

data.scan(/@(\w+).*\n(.+=.+,\n)+/) do
  match_data =  Regexp.last_match
  content = match_data[0].to_s
  content = content.scan(/.+=.+,\n/)
  output.puts("<table border='5'; width='400'>")
  output.puts("<tr><td colspan='2'>"+match_data[1]+"</td></tr>")
  content.each { |line| line.scan(/\s*(\w+)\s*=\s*["{]?(.+)(},|",|\b,)/) { |matched| 
      matched.pop
      output.puts("<tr>")
      if(matched[0] === "author" or matched[0] === "editor")
        matched[1] = "<ul><li>" + matched[1] + "</li></ul>"
        matched[1].gsub!(/\s+and\s+/, "</li><li>")
      end
      matched.each{ |attribute| output.puts("<td>"+attribute+"</td>") }
      output.puts("<tr>"); 
  } }

  output.puts("</table><br/>")
  output.puts("</body></html>")
end
