require 'rubygems'
require 'sinatra'

post '/messages' do
  puts "Got messages, size = #{request.content_length}"
  puts request.body.read
end

post '/rt_messages' do
  puts request.body.read
end
